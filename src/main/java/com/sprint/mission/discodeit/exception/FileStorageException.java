package com.sprint.mission.discodeit.exception;

public class FileStorageException extends RuntimeException {

    // 메세지와 원인을 같이 받음
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    // 메시지만 받는 생성자
    public FileStorageException(String message) {
        super(message);
    }
}