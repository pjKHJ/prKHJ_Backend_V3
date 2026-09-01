package dsm.prkhj.domain.auth.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GitHub은 인가 코드가 잘못돼도 HTTP 200을 주고 본문에 error를 담는다.
 * 그래서 상태 코드가 아니라 error 필드로 실패를 판단한다.
 */
public record GithubTokenResponse(
        @JsonProperty("access_token") String accessToken,
        String scope,
        String error
) {
}
