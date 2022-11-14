package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private String message;

    public RestApiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public RestApiException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
