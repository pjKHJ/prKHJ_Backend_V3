package dsm.prkhj.domain.auth.service;

import dsm.prkhj.domain.auth.controller.dto.response.LinkCodeResponse;
import dsm.prkhj.domain.auth.entity.ExtensionLink;
import dsm.prkhj.domain.auth.entity.LinkCode;
import dsm.prkhj.domain.auth.exception.AuthErrorCode;
import dsm.prkhj.domain.auth.repository.ExtensionLinkRepository;
import dsm.prkhj.domain.auth.repository.LinkCodeRepository;
import dsm.prkhj.domain.auth.repository.UserRepository;
import dsm.prkhj.global.exception.KHJException;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkCodeService {

    // link_codes.code = CHAR(12) 원문. 0/O/1/I 제외
    private static final char[] CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final int CODE_LENGTH = 12;
    private static final int MAX_CODE_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final LinkCodeRepository linkCodeRepository;
    private final ExtensionLinkRepository extensionLinkRepository;

    @Transactional
    public LinkCodeResponse getLinkCode(Long userId) {

        // 코드가 없으면 그 자리에서 발급
        LinkCode linkCode = linkCodeRepository.findByUserId(userId)
                .orElseGet(() -> issueLinkCode(userId));

        ExtensionLink extensionLink = extensionLinkRepository.findByUserId(userId)
                .filter(link -> link.getLinkCode().getId().equals(linkCode.getId())) // 이 코드로 붙은 연동만 (linked = true)
                .orElse(null);

        return LinkCodeResponse.of(linkCode, extensionLink);
    }

    // 기존 연동과 코드를 버리고 새로 발급
    // linked = false
    @Transactional
    public LinkCodeResponse resetLinkCode(Long userId) {
        extensionLi급nkRepository.deleteByUserId(userId);
        linkCodeRepository.deleteByUserId(userId);
        // uk_link_codes_user 충돌 방지
        // DELETE가 INSERT보다 먼저 나감

        // 영속성 트렌젝션
        linkCodeRepository.flush();

        return LinkCodeResponse.of(
                issueLinkCode(userId),
                null
        );
    }

    private LinkCode issueLinkCode(Long userId) {
        return linkCodeRepository.save(LinkCode.builder()
                .user(userRepository.getReferenceById(userId))
                .code(generateUniqueCode())
                .build());
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
            String code = randomCode();

            // 중복 확인
            if (!linkCodeRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new KHJException(AuthErrorCode.LINK_CODE_GENERATION_FAILED);
    }

    private String randomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS[SECURE_RANDOM.nextInt(CODE_CHARS.length)]);
        }
        return code.toString();
    }
}
