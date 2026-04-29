package com.sprint.mission.discodeit.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 조회 대상 없음
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(NoSuchElementException ex) {
        System.out.println("NoSuchElementException: " + ex.getMessage());

        return Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.out.println("IllegalArgumentException: " + ex.getMessage());

        return Map.of(
                "error", "BAD_REQUEST",
                "message", ex.getMessage()
        );
    }

    // 서버 예외
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAllException(Exception ex) {
        System.out.println("Exception: " + ex.getMessage());

        return Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "message", ex.getMessage()
        );
    }
}
