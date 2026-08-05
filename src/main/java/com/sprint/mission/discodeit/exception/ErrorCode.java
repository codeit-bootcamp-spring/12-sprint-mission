package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // Auth
  INVALID_USERNAME_OR_PASSWORD(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
  UNAUTHORIZED(401, "인증이 필요합니다."),
  ACCESS_DENIED(403, "접근 권한이 없습니다."),

  // User
  USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
  USERNAME_DUPLICATED(409, "이미 사용 중인 사용자 이름입니다."),
  EMAIL_DUPLICATED(409, "이미 사용 중인 이메일입니다."),

  // Channel
  CHANNEL_NOT_FOUND(404, "채널을 찾을 수 없습니다."),
  CHANNEL_NAME_DUPLICATED(409, "이미 사용 중인 채널 이름입니다."),
  PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(400, "비공개 채널은 수정할 수 없습니다."),

  // Message
  MESSAGE_NOT_FOUND(404, "메시지를 찾을 수 없습니다."),

  // ReadStatus
  READ_STATUS_NOT_FOUND(404, "읽음 상태를 찾을 수 없습니다."),
  READ_STATUS_DUPLICATED(409, "읽음 상태가 이미 존재합니다."),

  // BinaryContent / File
  BINARY_CONTENT_NOT_FOUND(404, "파일 정보를 찾을 수 없습니다."),
  BINARY_CONTENT_FILE_NOT_FOUND(404, "파일을 찾을 수 없습니다."),
  FILE_PROCESSING_ERROR(500, "파일 처리 중 오류가 발생했습니다."),
  FILE_STORAGE_ERROR(500, "파일 저장소 처리 중 오류가 발생했습니다.");

  private final int status;
  private final String message;
}
