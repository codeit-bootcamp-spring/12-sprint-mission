package com.sprint.mission.discodeit.exception.customclass;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
