package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidCredentialsException extends UserException {
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_USER_CREDENTIALS,
                ErrorCode.INVALID_USER_CREDENTIALS.getMessage(),
                Map.of());
    }

    public static InvalidCredentialsException wrongPassword() {
        return new InvalidCredentialsException();
    }
} 