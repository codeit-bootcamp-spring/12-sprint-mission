package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
       HttpStatus status = getStatus(exception.getErrorCode());

       return ResponseEntity.status(status).body(ErrorResponse.of(exception, status.value()));
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
       Map<String, Object> details = new LinkedHashMap<>();

       for(FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
           details.put(fieldError.getField(), fieldError.getDefaultMessage());
       }

       return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(ErrorResponse.of(
                       ErrorCode.INVALID_REQUEST,
                       details,
                       exception.getClass().getSimpleName(),
                       HttpStatus.BAD_REQUEST.value()
               ));
   }

   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
       Map<String, Object> details = new LinkedHashMap<>();
       details.put("message", exception.getMessage());

       return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(ErrorResponse.of(
                       ErrorCode.INVALID_REQUEST,
                       details,
                       exception.getClass().getSimpleName(),
                       HttpStatus.BAD_REQUEST.value()
               ));
   }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("Unhandled exception", exception);

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("message", "Unexpected server error occurred.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        details,
                        exception.getClass().getSimpleName(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(
                        ErrorCode.ACCESS_DENIED,
                        Map.of(),
                        exception.getClass().getSimpleName(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }

    private HttpStatus getStatus(ErrorCode errorCode) {

        return switch (errorCode) {
            case USER_NOT_FOUND,
                 CHANNEL_NOT_FOUND,
                 MESSAGE_NOT_FOUND,
                 READ_STATUS_NOT_FOUND,
                 BINARY_CONTENT_NOT_FOUND -> HttpStatus.NOT_FOUND;

            case USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;

            case PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED,
                 INVALID_PASSWORD,
                 INVALID_REQUEST -> HttpStatus.BAD_REQUEST;

            case BINARY_CONTENT_STORAGE_ERROR,
                 INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;

            case AUTHENTICATION_FAILED,
                 AUTHENTICATION_REQUIRED,
                 INVALID_REFRESH_TOKEN -> HttpStatus.UNAUTHORIZED;

            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
        };
    }
}
