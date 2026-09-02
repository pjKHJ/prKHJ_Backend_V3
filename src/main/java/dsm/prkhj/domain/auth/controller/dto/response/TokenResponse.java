package dsm.prkhj.domain.auth.controller.dto.response;

import java.time.OffsetDateTime;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        OffsetDateTime accessTokenExpiresAt
) {
}
