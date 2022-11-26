package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;

public class InvalidApiRequestException extends RestApiException {
    private final static ErrorCode ERROR_CODE = CommonErrorCode.API_ARGUMENT_NOT_VALID;

    public InvalidApiRequestException() {
        super(ERROR_CODE);
    }

    public InvalidApiRequestException(String message) {
        super(ERROR_CODE, message);
    }
}
