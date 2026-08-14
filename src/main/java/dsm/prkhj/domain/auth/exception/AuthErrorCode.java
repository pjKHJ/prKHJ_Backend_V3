package dsm.prkhj.domain.auth.exception;

import dsm.prkhj.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_REDIRECT_URI(HttpStatus.BAD_REQUEST, "AUTH_400_01", "허용되지 않은 redirectUri입니다."),
    INVALID_GITHUB_CODE_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_400_02", "GitHub 인가 코드가 없거나 형식이 올바르지 않습니다."),
    INVALID_LINK_CODE_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_400_03", "인증 코드가 없거나 형식이 올바르지 않습니다."),
    INVALID_GITHUB_CODE(HttpStatus.UNAUTHORIZED, "AUTH_401", "GitHub 인가 코드가 유효하지 않거나 만료되었습니다."),
    LINK_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_404", "존재하지 않거나 초기화된 인증 코드입니다."),
    LINK_CODE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_500", "인증 코드 생성에 실패했습니다."),
    GITHUB_OAUTH_UNAVAILABLE(HttpStatus.BAD_GATEWAY, "AUTH_502", "GitHub OAuth 서버와 통신하지 못했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
