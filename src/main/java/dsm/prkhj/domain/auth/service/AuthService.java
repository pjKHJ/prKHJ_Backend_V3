package dsm.prkhj.domain.auth.service;

import dsm.prkhj.domain.auth.client.GithubClient;
import dsm.prkhj.domain.auth.client.GithubUserResponse;
import dsm.prkhj.domain.auth.controller.dto.response.GithubAuthUrlResponse;
import dsm.prkhj.domain.auth.controller.dto.response.LoginResponse;
import dsm.prkhj.domain.auth.controller.dto.response.TokenResponse;
import dsm.prkhj.domain.auth.entity.User;
import dsm.prkhj.domain.auth.exception.AuthErrorCode;
import dsm.prkhj.domain.auth.exception.JwtErrorCode;
import dsm.prkhj.domain.auth.exception.UserErrorCode;
import dsm.prkhj.domain.auth.jwt.JwtProvider;
import dsm.prkhj.domain.auth.repository.UserRepository;
import dsm.prkhj.global.exception.KHJException;
import dsm.prkhj.global.redis.RefreshTokenStore;
import dsm.prkhj.global.security.TokenEncryptor;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthService {

    // +09:00 고정.
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final GithubClient githubClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final TokenEncryptor tokenEncryptor;

    private final String githubClientId;
    private final String githubAuthorizeUrl;
    private final String githubScope;
    private final List<String> allowedRedirectUris;
    private final String defaultRedirectUri;

    public AuthService(
            GithubClient githubClient,
            UserRepository userRepository,
            JwtProvider jwtProvider,
            RefreshTokenStore refreshTokenStore,
            TokenEncryptor tokenEncryptor,
            @Value("${github.client-id}") String githubClientId,
            @Value("${github.authorize-url}") String githubAuthorizeUrl,
            @Value("${github.scope}") String githubScope,
            @Value("${auth.allowed-redirect-uris}") List<String> allowedRedirectUris,
            @Value("${auth.default-redirect-uri}") String defaultRedirectUri
    ) {
        this.githubClient = githubClient;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.tokenEncryptor = tokenEncryptor;
        this.githubClientId = githubClientId;
        this.githubAuthorizeUrl = githubAuthorizeUrl;
        this.githubScope = githubScope;
        this.allowedRedirectUris = allowedRedirectUris;
        this.defaultRedirectUri = defaultRedirectUri;
    }

    public GithubAuthUrlResponse getGithubAuthorizationUrl(String redirectUri) {
        String targetRedirectUri =
                (redirectUri == null) ? defaultRedirectUri : redirectUri;
        if (!allowedRedirectUris.contains(targetRedirectUri)) {
            throw new KHJException(AuthErrorCode.INVALID_REDIRECT_URI);
        }

        String state = generateState();
        String authorizationUrl = UriComponentsBuilder.fromUriString(githubAuthorizeUrl)
                .queryParam("client_id", githubClientId)
                .queryParam("scope", githubScope)
                .queryParam("state", state)
                .queryParam("redirect_uri", targetRedirectUri)
                .build()
                .encode()
                .toUriString();

        return new GithubAuthUrlResponse(authorizationUrl, state);
    }

    @Transactional
    public LoginResponse loginWithGithub(String code) {
        if (!StringUtils.hasText(code)) {
            throw new KHJException(AuthErrorCode.INVALID_GITHUB_CODE_FORMAT);
        }

        String githubAccessToken = githubClient.exchangeCodeForAccessToken(code);
        GithubUserResponse githubUser = githubClient.getUser(githubAccessToken);

        User existing = userRepository.findByGithubUserId(githubUser.id()).orElse(null);
        boolean isNewUser = (existing == null);
        User user = isNewUser
                ? userRepository.save(
                        User.builder()
                        .githubUserId(githubUser.id())
                        .githubLogin(githubUser.login())
                        .avatarUrl(githubUser.avatarUrl())
                        .build()
                )
                : existing;
        user.syncGithubProfile(githubUser.login(), githubUser.avatarUrl());
        // repo scope 토큰. 커밋 기능에서 쓰려면 보관해야 하는데 평문은 안됨.
        user.updateGithubAccessToken(tokenEncryptor.encrypt(githubAccessToken));

        return LoginResponse.of(issueTokens(user), user, isNewUser);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new KHJException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        // GETDEL로 원자적으로 소비함
        // 같은 RT가 동시에 들어와도 하나만 값을 받음
        Long userId = validRefreshTokenId(refreshToken)
                .map(refreshTokenStore::consumeUserId)
                .filter(stored -> stored.equals(jwtProvider.getUserId(refreshToken)))
                .orElseThrow(() -> new KHJException(JwtErrorCode.INVALID_OR_EXPIRED_REFRESH_TOKEN));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new KHJException(UserErrorCode.USER_NOT_FOUND));

        return issueTokens(user);
    }

    public void logout(Long authenticatedUserId, String refreshToken) {
        // 남의 RT를 폐기하지 못하도록 AT 주체와 일치할 때만 지운다.
        // 이미 만료/폐기된 RT여도 로그아웃은 멱등하게 204.
        resolveRefreshTokenOwner(refreshToken)
                .filter(authenticatedUserId::equals)
                .ifPresent(userId -> refreshTokenStore.delete(jwtProvider.getTokenId(refreshToken)));
    }

    /**
     * RT가 서명/만료상 유효하고 Redis에 살아있으면 그 소유자 id를 준다.
     * 실패는 전부 빈 값. 호출부가 JWT_401로 올릴지(A3) 무시할지(A4) 정한다.
     */
    private Optional<Long> resolveRefreshTokenOwner(String refreshToken) {
        return validRefreshTokenId(refreshToken)
                .map(refreshTokenStore::findUserId)
                .filter(stored -> stored.equals(jwtProvider.getUserId(refreshToken)));
    }

    /** 서명/만료가 유효하고 jti가 있는 RT의 tokenId. jti가 없다 = RT가 아니라 AT를 보냈다 */
    private Optional<String> validRefreshTokenId(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) || !jwtProvider.validateToken(refreshToken)) {
            return Optional.empty();
        }
        return Optional.ofNullable(jwtProvider.getTokenId(refreshToken));
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getGithubLogin(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());
        refreshTokenStore.save(jwtProvider.getTokenId(refreshToken), user.getId());

        OffsetDateTime expiresAt =
                OffsetDateTime.ofInstant(jwtProvider.getExpiration(accessToken), KST);
        return new TokenResponse(accessToken, refreshToken, expiresAt);
    }

    private String generateState() {

        // UUID 문자열에는 이픈이 섞여있음.
        // 히후 콜백 단계에서 세션이나 별도 저장소에 발급한 state를 저장해두고 비교하는 로직이 필요함. 기억하라고
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
