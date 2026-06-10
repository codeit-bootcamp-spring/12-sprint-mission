package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleException(DiscodeitException e) {
    log.error("커스텀 예외 발생 : code={}, message={}, detail={} ",e.getErrorCode(), e.getMessage(), e.getDetails());
    HttpStatus httpStatus = parseHttpStatus(e);
    ErrorResponse errorResponse = new ErrorResponse(e,httpStatus.value());
    return ResponseEntity
            .status(httpStatus)
            .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
      Map<String, Object> details = new LinkedHashMap<>();

      String objectName = e.getBindingResult().getObjectName();

      List<Map<String,Object>> errors = e.getBindingResult().getFieldErrors()
              .stream()
              .map(fieldError ->{
                  Map<String,Object> fieldDetails = new LinkedHashMap<>();
                  fieldDetails.put("field", fieldError.getField());
                  fieldDetails.put("message", fieldError.getDefaultMessage());

                  log.warn(
                          "검증 오류 발생 : objectName={}, field={}, message={}",
                          objectName,
                          fieldError.getField(),
                          fieldError.getDefaultMessage()
                  );

                  return fieldDetails;
              })
              .toList();

      details.put("ObjectName", objectName);
      details.put("errors", errors);

      ErrorResponse errorResponse = new ErrorResponse(
              e,
              HttpStatus.BAD_REQUEST.value(),
              details
      );

      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생 : {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(e,HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity
        .status(errorResponse.getStatus())
        .body(errorResponse);
  }

  HttpStatus parseHttpStatus(DiscodeitException e) {
    ErrorCode errorCode = e.getErrorCode();
    return switch (errorCode){
      // 404 Not Found
      case USER_NOT_FOUND,
           CHANNEL_NOT_FOUND,
           MESSAGE_NOT_FOUND,
           USER_STATUS_NOT_FOUND,
           READ_STATUS_NOT_FOUND,
           BINARY_CONTENT_NOT_FOUND -> HttpStatus.NOT_FOUND;

      // 409 Conflict
      case DUPLICATE_USER,
           DUPLICATE_CHANNEL,
           DUPLICATE_USER_STATUS,
           DUPLICATE_READ_STATUS,
           DUPLICATE_BINARY_CONTENT-> HttpStatus.CONFLICT;

      // 403 forbidden
      case MESSAGE_UPDATE_FORBIDDEN,
           MESSAGE_DELETE_FORBIDDEN,
           CHANNEL_ACCESS_DENIED -> HttpStatus.FORBIDDEN;

      // 401 Unauthorized
      case INVALID_USER_CREDENTIALS ->HttpStatus.UNAUTHORIZED;

      // 400 Bad Request
      case INVALID_USER_REQUEST,
           INVALID_FILE_REQUEST,
           INVALID_READ_STATUS_REQUEST,
           INVALID_USER_STATUS_REQUEST,
           PRIVATE_CHANNEL_UPDATE -> HttpStatus.BAD_REQUEST;

      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
