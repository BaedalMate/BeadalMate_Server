package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;

public class InvalidParameterException extends RestApiException {
    private final static ErrorCode ERROR_CODE = CommonErrorCode.INVALID_PARAMETER;

    public InvalidParameterException() {
        super(ERROR_CODE);
    }
}
