package baedalmate.baedalmate.errors.exceptions;


import baedalmate.baedalmate.errors.errorcode.ErrorCode;
import baedalmate.baedalmate.errors.errorcode.UserErrorCode;

public class ExpiredRefreshTokenException extends RestApiException {
    private final static ErrorCode ERROR_CODE = UserErrorCode.EXPIRED_REFRESH_TOKEN;

    public ExpiredRefreshTokenException() {
        super(ERROR_CODE);
    }

}
