package dsm.prkhj.domain.extension.controller.dto.response;

import dsm.prkhj.domain.auth.entity.ExtensionLink;
import dsm.prkhj.domain.auth.entity.User;
import dsm.prkhj.global.time.Kst;
import java.time.OffsetDateTime;

/** 연동 행이 없으면 linked=false, 제출 이력이 없으면 recentTransfer=null. */
public record ExtensionStatusResponse(
        boolean linked,
        String githubLogin,
        String avatarUrl,
        OffsetDateTime linkedAt,
        RecentTransfer recentTransfer
) {
    // submission 도메인 개발 필요 -> 현재는 항상 null
    public record RecentTransfer(
            String problemTitle,
            String siteName,
            String collectStatus,
            OffsetDateTime collectedAt
    ) {
    }

    public static ExtensionStatusResponse of(User user, ExtensionLink link) {
        return new ExtensionStatusResponse(
                link != null,
                user.getGithubLogin(),
                user.getAvatarUrl(),
                link == null ? null : Kst.toOffset(link.getLinkedAt()),
                null); // submissions의 collected_at DESC 최신 1건으로 채운다
    }
}
