package dsm.prkhj.global.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path
) {

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return of(errorCode, errorCode.getMessage(), path);
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return new ErrorResponse(Instant.now(), errorCode.getStatus().value(), errorCode.getCode(), message, path);
    }
}
