package baedalmate.baedalmate.errors.exceptions;

import baedalmate.baedalmate.errors.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private String message;
}
