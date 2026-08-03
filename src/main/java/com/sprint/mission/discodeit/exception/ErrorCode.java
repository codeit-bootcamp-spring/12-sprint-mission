package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // Auth
  INVALID_CREDENTIALS("아이디 또는 비밀번호가 올바르지 않습니다."),
  INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다. 다시 로그인해 주세요."),
  // User
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_USER("이미 존재하는 사용자입니다."),
  // Channel
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE("PRIVATE 채널은 수정할 수 없습니다."),
  // Message
  MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
  // BinaryContent
  BINARY_CONTENT_NOT_FOUND("첨부파일을 찾을 수 없습니다."),
  // ReadStatus
  READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
  DUPLICATE_READ_STATUS("이미 존재하는 읽음 상태입니다."),
  // Storage
  STORAGE_FAILURE("파일 저장/읽기에 실패했습니다.");

  private final String message;
}
