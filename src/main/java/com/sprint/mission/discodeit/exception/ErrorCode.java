package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_REQUEST("잘못된 요청입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
    STATUS_NOT_FOUND("상태정보를 찾을 수 없습니다."),
    CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    ACCESS_DENIED("접근이 거부되었습니다."),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다.");

    private final String message;

    ErrorCode(String message){this.message = message;}
}
