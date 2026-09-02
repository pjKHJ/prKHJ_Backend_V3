package dsm.prkhj.domain.auth.controller.dto.response;

import dsm.prkhj.domain.auth.entity.Role;
import dsm.prkhj.domain.auth.entity.User;
import java.time.OffsetDateTime;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        OffsetDateTime accessTokenExpiresAt,
        UserInfo user
) {

    public record UserInfo(
            Long userId,
            String githubLogin,
            String avatarUrl,
            Role role,
            boolean isNewUser
    ) {
    }

    public static LoginResponse of(TokenResponse tokens, User user, boolean isNewUser) {
        return new LoginResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiresAt(),
                new UserInfo(user.getId(), user.getGithubLogin(), user.getAvatarUrl(), user.getRole(), isNewUser)
        );
    }
}
