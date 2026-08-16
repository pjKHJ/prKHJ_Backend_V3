package dsm.prkhj.domain.auth.controller;

import dsm.prkhj.domain.auth.dto.response.GithubAuthUrlResponse;
import dsm.prkhj.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/github/url")
    public GithubAuthUrlResponse getGithubAuthorizationUrl(
            @RequestParam(required = false) String redirectUri
    ) {
        return authService.getGithubAuthorizationUrl(redirectUri);
    }
}
