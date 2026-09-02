package dsm.prkhj.domain.auth.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUserResponse(
        Long id,
        String login,
        @JsonProperty("avatar_url") String avatarUrl
) {
}
