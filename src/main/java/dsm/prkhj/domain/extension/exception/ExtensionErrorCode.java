package dsm.prkhj.domain.extension.exception;

import dsm.prkhj.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExtensionErrorCode implements ErrorCode {

    INVALID_EXTENSION_TOKEN(HttpStatus.UNAUTHORIZED, "EXT_401", "익스텐션 토큰이 없거나 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
