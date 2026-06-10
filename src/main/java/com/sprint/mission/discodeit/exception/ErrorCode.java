package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),
    USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS("사용자 상태가 이미 존재합니다."),

    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED("PRIVATE 채널은 수정할 수 없습니다."),

    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

    READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),

    BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    BINARY_CONTENT_STORAGE_ERROR("파일 저장소 처리 중 오류가 발생했습니다."),

    INVALID_PASSWORD("비밀번호가 일치하지 않습니다."),

    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.");

    private final String message;
}