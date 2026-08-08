package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // User 관련 에러 코드
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  USER_DUPLICATE("이미 존재하는 사용자입니다."),
  INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),
  AUTHENTICATION_FAILED("인증 실패"),

  // Channel 관련 에러 코드
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE("개인 채널은 수정할 수 없습니다."),
  CHANNEL_ACCESS_DENIED("채널에 접근할 권한이 없습니다."),

  // Message 관련 에러 코드
  MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

  // ReadStatus 관련 에러 코드
  READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
  READ_STATUS_DUPLICATE("이미 존재하는 읽음 상태입니다."),

  // BinaryContent 관련 에러 코드
  BINARY_CONTENT_NOT_FOUND("바이너리 콘텐츠를 찾을 수 없습니다."),

  // Auth 관련 에러 코드
  INVALID_REFRESH_TOKEN("유효하지 않거나 만료된 리프레시 토큰입니다."),
  JWT_GENERATION_FAILED("토큰 발급에 실패했습니다."),

  // Server 에러 코드
  INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
  INVALID_REQUEST("잘못된 요청입니다.");

  private final String message;
}