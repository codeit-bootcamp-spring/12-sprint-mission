package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class ErrorResponse {
    private final Instant timestamp;
    private final String code;
    private final String message;
    private final Map<String, Object> details;
    private final String exceptionType; // 발생한 예외클래스 이름
    private final int status; // http 상태코드

    public ErrorResponse(DiscodeitException exception, int status) {
        this(
                Instant.now(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                exception.getDetails(),
                exception.getClass().getSimpleName(),
                status
        );
    }

    public ErrorResponse(Exception exception, int status) {
        this(
                Instant.now(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                new HashMap<>(),
                exception.getClass().getSimpleName(),
                status
        );
    }

    public ErrorResponse(Exception exception, int status, Map<String, Object> details) {
        this(
                Instant.now(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                details,
                exception.getClass().getSimpleName(),
                status
        );
    }
}
