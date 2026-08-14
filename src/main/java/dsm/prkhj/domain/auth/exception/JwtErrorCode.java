package dsm.prkhj.domain.auth.exception;

import dsm.prkhj.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {

    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "JWT_400", "refreshToken 누락/형식이 잘못되었습니다."),
    INVALID_OR_EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_401", "refreshToken 만료/위조/서버 저장값과 불일치합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
