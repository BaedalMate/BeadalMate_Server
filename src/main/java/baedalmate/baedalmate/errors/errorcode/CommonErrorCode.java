package baedalmate.baedalmate.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "Invalid parameter included in page object"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    EXIST_ORDER(HttpStatus.BAD_REQUEST, "User already ordered"),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image not exist"),
    API_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "Api request body invalid"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
