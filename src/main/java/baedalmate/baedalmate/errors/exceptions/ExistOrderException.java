package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;

public class ExistOrderException extends RestApiException {

    private final static ErrorCode ERROR_CODE = CommonErrorCode.EXIST_ORDER;

    public ExistOrderException() {
        super(ERROR_CODE);
    }
}
