package dsm.prkhj.domain.auth.controller;

import dsm.prkhj.domain.auth.controller.dto.response.LinkCodeResponse;
import dsm.prkhj.domain.auth.jwt.UserPrincipal;
import dsm.prkhj.domain.auth.service.LinkCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/link-code")
@RequiredArgsConstructor
public class LinkCodeController {

    private final LinkCodeService linkCodeService;

    @GetMapping
    public LinkCodeResponse getLinkCode(@AuthenticationPrincipal UserPrincipal principal) {
        return linkCodeService.getLinkCode(principal.id());
    }

    @PostMapping("/reset")
    public LinkCodeResponse resetLinkCode(@AuthenticationPrincipal UserPrincipal principal) {
        return linkCodeService.resetLinkCode(principal.id());
    }
}
