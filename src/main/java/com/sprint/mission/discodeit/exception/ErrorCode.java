package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // User
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_USER("이미 존재하는 사용자입니다."),
    INVALID_USER_REQUEST("잘못된 사용자 요청입니다."),
    INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),

    // Message
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
    MESSAGE_UPDATE_FORBIDDEN("메시지를 수정할 권한이 없습니다."),
    MESSAGE_DELETE_FORBIDDEN("메시지를 삭제할 권한이 없습니다."),

    // Channel
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    DUPLICATE_CHANNEL("이미 존재하는 채널입니다."),
    PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다."),
    CHANNEL_ACCESS_DENIED("채널에 접근할 권한이 없습니다."),

    // ReadStatus
    READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
    DUPLICATE_READ_STATUS("이미 존재하는 읽음 상태입니다."),
    INVALID_READ_STATUS_REQUEST("잘못된 읽음 상태 요청입니다."),

    // UserStatus
    USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),
    DUPLICATE_USER_STATUS("이미 존재하는 사용자 상태입니다."),
    INVALID_USER_STATUS_REQUEST("잘못된 사용자 상태 요청입니다."),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다."),
    FILE_DOWNLOAD_FAILED("파일 다운로드에 실패했습니다."),
    INVALID_FILE_REQUEST("잘못된 파일 요청입니다."),
    DUPLICATE_BINARY_CONTENT("이미 존재하는 파일입니다.");


    private final String message;
}
