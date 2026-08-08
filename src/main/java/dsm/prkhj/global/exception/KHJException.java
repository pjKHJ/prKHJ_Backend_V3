package dsm.prkhj.global.exception;

import lombok.Getter;

@Getter
public class KHJException extends RuntimeException {

    private final ErrorCode errorCode;

    public KHJException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public KHJException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
