package dsm.prkhj.domain.auth.service;

import dsm.prkhj.domain.auth.dto.response.GithubAuthUrlResponse;
import dsm.prkhj.domain.auth.exception.AuthErrorCode;
import dsm.prkhj.global.exception.KHJException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthService {

    private final String githubClientId;
    private final String githubAuthorizeUrl;
    private final String githubScope;
    private final List<String> allowedRedirectUris;
    private final String defaultRedirectUri;

    public AuthService(
            @Value("${github.client-id}") String githubClientId,
            @Value("${github.authorize-url}") String githubAuthorizeUrl,
            @Value("${github.scope}") String githubScope,
            @Value("${auth.allowed-redirect-uris}") List<String> allowedRedirectUris,
            @Value("${auth.default-redirect-uri}") String defaultRedirectUri
    ) {
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
                .build()
                .toUriString();

        return new GithubAuthUrlResponse(authorizationUrl, state);
    }

    private String generateState() {

        // UUID 문자열에는 이픈이 섞여있음.
        // 히후 콜백 단계에서 세션이나 별도 저장소에 발급한 state를 저장해두고 비교하는 로직이 필요함. 기억하라고
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
