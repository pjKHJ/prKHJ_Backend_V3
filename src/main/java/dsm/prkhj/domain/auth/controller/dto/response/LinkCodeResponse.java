package dsm.prkhj.domain.auth.controller.dto.response;

import dsm.prkhj.domain.auth.entity.ExtensionLink;
import dsm.prkhj.domain.auth.entity.LinkCode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public record LinkCodeResponse(
        String code,
        OffsetDateTime issuedAt,
        boolean linked,
        OffsetDateTime linkedAt
) {

    // issuedAt/linkedAt은 +09:00 고정
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** extensionLink가 null이면 미연동. */
    public static LinkCodeResponse of(LinkCode linkCode, ExtensionLink extensionLink) {

        return new LinkCodeResponse(
                formatCode(linkCode.getCode()),
                toKst(linkCode.getIssuedAt()),
                extensionLink != null,
                extensionLink == null ? null : toKst(extensionLink.getLinkedAt())
        );
    }

    private static String formatCode(String code) {

        // 저장은 12자 원문, 노출은 XXXX-XXXX-XXXX.
        return code.substring(0, 4) + "-" + code.substring(4, 8) + "-" + code.substring(8);
    }

    private static OffsetDateTime toKst(LocalDateTime dateTime) {
        return dateTime.atZone(KST).toOffsetDateTime();
    }
}
