package com.sprint.mission.discodeit.exception.customclass;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(String message) {
        super(message);
    }
}
