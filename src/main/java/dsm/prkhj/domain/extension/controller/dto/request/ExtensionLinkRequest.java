package dsm.prkhj.domain.extension.controller.dto.request;

/** 웹에서 발급받은 인증 코드. 사용자가 옮겨 적은 그대로라 하이픈·소문자가 섞여 올 수 있다. */
public record ExtensionLinkRequest(String code) {
}
