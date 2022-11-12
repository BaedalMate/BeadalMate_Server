package baedalmate.baedalmate.errors.handler;

import baedalmate.baedalmate.errors.errorcode.CommonErrorCode;
import baedalmate.baedalmate.errors.errorcode.ErrorCode;
import baedalmate.baedalmate.errors.exceptions.ExistOrderException;
import baedalmate.baedalmate.errors.exceptions.InvalidParameterException;
import baedalmate.baedalmate.errors.exceptions.ResourceNotFoundException;
import baedalmate.baedalmate.errors.exceptions.RestApiException;
import baedalmate.baedalmate.errors.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
//        ErrorCode errorCode = e.getErrorCode();
//        return handleExceptionInternal(errorCode);
//    }
//
//    @ExceptionHandler(ExistOrderException.class)
//    public ResponseEntity<Object> handleExistOrderException(ExistOrderException e) {
//        ErrorCode errorCode = e.getErrorCode();
//        return handleExceptionInternal(errorCode);
//    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        if(e.getMessage().isEmpty())
            return handleExceptionInternal(errorCode);
        else
            return handleExceptionInternal(errorCode, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(e, errorCode);
    }

//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<Object> handleAllException(Exception ex) {
//        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
//        return handleExceptionInternal(errorCode);
//    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(makeErrorResponse(errorCode));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(makeErrorResponse(errorCode, message));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.getHttpStatus().value())
                .message(message)
                .build();
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(makeErrorResponse(e, errorCode));
    }

    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .errors(validationErrorList)
                .build();
    }
}
