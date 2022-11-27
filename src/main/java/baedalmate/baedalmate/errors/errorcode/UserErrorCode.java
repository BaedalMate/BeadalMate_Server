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
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
    WRONG_TOKEN(HttpStatus.BAD_REQUEST, "wrong token"),
    EXPIRED_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "Expired social token"),
    WRONG_TYPE_SIGNATURE(HttpStatus.UNAUTHORIZED, "Wrong type signature"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Expired access token"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid access token"),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Refresh token doesn't exist"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Expired refresh token"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
