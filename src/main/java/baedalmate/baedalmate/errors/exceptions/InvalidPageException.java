package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;

public class InvalidPageException extends RestApiException {
    private final static ErrorCode ERROR_CODE = CommonErrorCode.INVALID_PAGE;

    public InvalidPageException() {
        super(ERROR_CODE);
    }

    public InvalidPageException(String message) {
        super(ERROR_CODE, message);
    }
}
