package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;

public class ImageNotFoundException extends RestApiException {
    private final static ErrorCode ERROR_CODE = CommonErrorCode.IMAGE_NOT_FOUND;

    public ImageNotFoundException() {
        super(ERROR_CODE);
    }
}
