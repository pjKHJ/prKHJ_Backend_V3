package dsm.prkhj.domain.auth.exception;

import dsm.prkhj.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USR_404_01", "존재하지 않는 상용자입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
