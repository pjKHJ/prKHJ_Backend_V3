package dsm.prkhj.domain.extension.controller;

import dsm.prkhj.domain.extension.controller.dto.request.ExtensionLinkRequest;
import dsm.prkhj.domain.extension.controller.dto.response.ExtensionLinkResponse;
import dsm.prkhj.domain.extension.controller.dto.response.ExtensionStatusResponse;
import dsm.prkhj.domain.extension.service.ExtensionLinkService;
import dsm.prkhj.global.security.ExtensionPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/extension/link")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionLinkService extensionLinkService;

    @PostMapping
    public ExtensionLinkResponse link(@RequestBody ExtensionLinkRequest request) {
        return extensionLinkService.link(request.code());
    }

    @GetMapping
    public ExtensionStatusResponse getStatus(@AuthenticationPrincipal ExtensionPrincipal principal) {
        return extensionLinkService.getStatus(principal.userId());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlink(@AuthenticationPrincipal ExtensionPrincipal principal) {
        extensionLinkService.unlink(principal.userId());
    }
}
