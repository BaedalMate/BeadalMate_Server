package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;

public class MethodArgumentNotValidException extends RestApiException {
    private final static ErrorCode ERROR_CODE = CommonErrorCode.API_ARGUMENT_NOT_VALID;

    public MethodArgumentNotValidException() {
        super(ERROR_CODE);
    }
}
