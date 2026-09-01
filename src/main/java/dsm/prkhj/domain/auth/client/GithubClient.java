package dsm.prkhj.domain.auth.client;

import dsm.prkhj.domain.auth.exception.AuthErrorCode;
import dsm.prkhj.global.exception.KHJException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GithubClient {

    private final RestClient restClient = RestClient.create();

    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;
    private final String userUrl;

    public GithubClient(
            @Value("${github.client-id}") String clientId,
            @Value("${github.client-secret}") String clientSecret,
            @Value("${github.token-url}") String tokenUrl,
            @Value("${github.user-url}") String userUrl
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
        this.userUrl = userUrl;
    }

    public String exchangeCodeForAccessToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("code", code);

        GithubTokenResponse response = call(
                () -> restClient.post()

                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(GithubTokenResponse.class)
        );

        if (response == null || response.accessToken() == null) {
            throw new KHJException(AuthErrorCode.INVALID_GITHUB_CODE);
        }
        return response.accessToken();
    }

    public GithubUserResponse getUser(String accessToken) {
        GithubUserResponse response = call(
                () -> restClient.get()

                .uri(userUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(GithubUserResponse.class)
        );

        if (response == null || response.id() == null) {
            throw new KHJException(AuthErrorCode.INVALID_GITHUB_CODE);
        }
        return response;
    }

    private <T> T call(java.util.function.Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientException e) {
            throw new KHJException(AuthErrorCode.GITHUB_OAUTH_UNAVAILABLE);
        }
    }
}
