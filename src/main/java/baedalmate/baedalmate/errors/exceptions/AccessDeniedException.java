package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.ErrorCode;
import baedalmate.baedalmate.errors.errorcode.UserErrorCode;

public class AccessDeniedException extends RestApiException {
    private final static ErrorCode ERROR_CODE = UserErrorCode.ACCESS_DENIED;

    public AccessDeniedException() {
        super(ERROR_CODE);
    }

    public AccessDeniedException(String message) {
        super(ERROR_CODE, message);
    }
}
