package com.sprint.mission.discodeit.exception;

public class UserAlreadyException extends RuntimeException {
    public UserAlreadyException(String message) {
        super(message);
    }
}
