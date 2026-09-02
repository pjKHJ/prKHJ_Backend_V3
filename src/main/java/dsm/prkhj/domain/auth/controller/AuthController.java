package dsm.prkhj.domain.auth.controller;

import dsm.prkhj.domain.auth.controller.dto.request.GithubLoginRequest;
import dsm.prkhj.domain.auth.controller.dto.request.RefreshTokenRequest;
import dsm.prkhj.domain.auth.controller.dto.response.GithubAuthUrlResponse;
import dsm.prkhj.domain.auth.controller.dto.response.LoginResponse;
import dsm.prkhj.domain.auth.controller.dto.response.TokenResponse;
import dsm.prkhj.domain.auth.jwt.UserPrincipal;
import dsm.prkhj.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping("/github")
    public LoginResponse loginWithGithub(@RequestBody GithubLoginRequest request) {
        return authService.loginWithGithub(request.code());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(principal.id(), request.refreshToken());
    }
}
