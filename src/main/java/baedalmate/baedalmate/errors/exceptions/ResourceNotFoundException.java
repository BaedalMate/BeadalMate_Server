package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;

public class ResourceNotFoundException extends RestApiException{
    private final static ErrorCode ERROR_CODE = CommonErrorCode.RESOURCE_NOT_FOUND;

    public ResourceNotFoundException() {
        super(ERROR_CODE);
    }
}