package dsm.prkhj.domain.auth.client;

import dsm.prkhj.domain.auth.exception.AuthErrorCode;
import dsm.prkhj.global.exception.KHJException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;

@Component
public class GithubClient {

    // 리다이렉트를 따라가면 https -> http 로 내려가며 client-secret/access-token 이 평문 노출될 수 있음
    private final RestClient restClient = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory(
                    HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()))
            .build();

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
        this.tokenUrl = requireHttps("github.token-url", tokenUrl);
        this.userUrl = requireHttps("github.user-url", userUrl);
    }

    private static String requireHttps(String property, String url) {
        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(property + " is not a valid URL: " + url, e);
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new IllegalArgumentException(property + " must be an absolute https URL: " + url);
        }
        return url;
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
