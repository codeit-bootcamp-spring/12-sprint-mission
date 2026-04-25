package com.sprint.mission.discodeit.exception.customclass;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException(String message) {
        super(message);
    }
}
