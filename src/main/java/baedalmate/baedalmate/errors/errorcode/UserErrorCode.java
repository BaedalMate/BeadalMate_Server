package baedalmate.baedalmate.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    INACTIVE_USER(HttpStatus.FORBIDDEN, "User is inactive"),
    WRONG_TYPE_TOKEN(HttpStatus.BAD_REQUEST, "Wrong type token"),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "Token expired"),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "Unsupported token"),
    UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "Unknown error"),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "access_denied"),
    WRONG_TOKEN(HttpStatus.BAD_REQUEST, "wrong token"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
