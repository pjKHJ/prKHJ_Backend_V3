package dsm.prkhj.domain.extension.controller.dto.response;

import dsm.prkhj.domain.auth.entity.ExtensionLink;
import dsm.prkhj.domain.auth.entity.User;
import dsm.prkhj.global.time.Kst;
import java.time.OffsetDateTime;

public record ExtensionLinkResponse(
        String extensionToken,
        UserInfo user,
        OffsetDateTime linkedAt
) {

    public record UserInfo(Long userId, String githubLogin, String avatarUrl) {
    }

    public static ExtensionLinkResponse of(String extensionToken, User user, ExtensionLink link) {
        return new ExtensionLinkResponse(extensionToken,
                new UserInfo(user.getId(), user.getGithubLogin(), user.getAvatarUrl()),
                Kst.toOffset(link.getLinkedAt()));
    }
}
