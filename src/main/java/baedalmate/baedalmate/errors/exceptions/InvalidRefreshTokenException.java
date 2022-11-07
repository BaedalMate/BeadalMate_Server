package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.ErrorCode;
import baedalmate.baedalmate.errors.errorcode.UserErrorCode;

public class InvalidRefreshTokenException extends RestApiException {
    private final static ErrorCode ERROR_CODE = UserErrorCode.INVALID_REFRESH_TOKEN;

    public InvalidRefreshTokenException() {
        super(ERROR_CODE);
    }
}
