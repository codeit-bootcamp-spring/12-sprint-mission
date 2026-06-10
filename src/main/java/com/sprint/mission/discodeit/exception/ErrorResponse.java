package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType,
        int status
) {

    public static ErrorResponse of(DiscodeitException exception, int status) {
        return new ErrorResponse(
                exception.getTimestamp(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                exception.getDetails(),
                exception.getClass().getSimpleName(),
                status
        );
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            Map<String, Object> details,
            String exceptionType,
            int status
    ) {
        return new ErrorResponse(
                Instant.now(),
                errorCode.name(),
                errorCode.getMessage(),
                details,
                exceptionType,
                status
        );
    }
}