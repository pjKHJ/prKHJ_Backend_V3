package dsm.prkhj.domain.extension.service;

import dsm.prkhj.domain.auth.entity.ExtensionLink;
import dsm.prkhj.domain.auth.entity.LinkCode;
import dsm.prkhj.domain.auth.entity.User;
import dsm.prkhj.domain.auth.exception.AuthErrorCode;
import dsm.prkhj.domain.auth.repository.ExtensionLinkRepository;
import dsm.prkhj.domain.auth.repository.LinkCodeRepository;
import dsm.prkhj.domain.auth.repository.UserRepository;
import dsm.prkhj.domain.extension.controller.dto.response.ExtensionLinkResponse;
import dsm.prkhj.domain.extension.controller.dto.response.ExtensionStatusResponse;
import dsm.prkhj.domain.extension.exception.ExtensionErrorCode;
import dsm.prkhj.global.exception.KHJException;
import dsm.prkhj.global.redis.ExtensionTokenStore;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExtensionLinkService {

    // 저장 형식
    private static final Pattern LINK_CODE = Pattern.compile("[A-HJ-NP-Z2-9]{12}"); // 0/O/1/I 제외

    // Redis 저장 접두사
    private static final String TOKEN_PREFIX = "ext_";
    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    private final LinkCodeRepository linkCodeRepository;
    private final ExtensionLinkRepository extensionLinkRepository;
    private final UserRepository userRepository;
    private final ExtensionTokenStore extensionTokenStore;

    @Transactional
    public ExtensionLinkResponse link(String rawCode) {

        // 하이픈 제거
        String code = normalize(rawCode);

        LinkCode linkCode = linkCodeRepository.findByCodeForUpdate(code) // 비관적락
                .orElseThrow(() -> new KHJException(AuthErrorCode.LINK_CODE_NOT_FOUND));
        User user = linkCode.getUser();

        extensionLinkRepository.deleteByUserId(user.getId());
        extensionLinkRepository.flush();

        ExtensionLink link = extensionLinkRepository.saveAndFlush(
                ExtensionLink.builder()
                        .user(user)
                        .linkCode(linkCode)
                        .build()
        );

        String token = TOKEN_PREFIX + HexFormat.of().formatHex(randomBytes());
        extensionTokenStore.save(token, user.getId());
        return ExtensionLinkResponse.of(token, user, link);
    }

    @Transactional(readOnly = true)
    public ExtensionStatusResponse getStatus(Long userId) {
        // 토큰은 살아 있는데 사용자가 없으면 재연동이 필요한 상태다
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new KHJException(ExtensionErrorCode.INVALID_EXTENSION_TOKEN));

        return ExtensionStatusResponse.of(
                user,
                extensionLinkRepository.findByUserId(userId)
                        .orElse(null)
        );
    }

    @Transactional
    public void unlink(Long userId) {
        extensionLinkRepository.deleteByUserId(userId);
        extensionTokenStore.deleteByUserId(userId);
    }

    // 사용자가 보는 코드는 XXXX-XXXX-XXXX -> 하이픈을 제거해야 함
    private static String normalize(String rawCode) {
        String code;

        if (rawCode == null) {
            code = "";
        } else {
            code = rawCode.strip().replace("-", "").toUpperCase(Locale.ROOT);
        }

        if (!LINK_CODE.matcher(code).matches()) {
            throw new KHJException(AuthErrorCode.INVALID_LINK_CODE_FORMAT);
        }
        return code;
    }

    private static byte[] randomBytes() {
        byte[] bytes = new byte[TOKEN_BYTES];

        // bytes의 모든 index의 요소를 난수로 채움
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }
}
