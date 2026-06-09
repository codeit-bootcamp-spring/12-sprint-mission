package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // 로그인
  INVALID_LOGIN("아이디 또는 비밀번호가 올바르지 않습니다."),

  // User
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_EMAIL("이미 사용 중인 이메일입니다."),
  DUPLICATE_USERNAME("이미 사용 중인 사용자 이름입니다."),

  // UserStatus
  USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),
  DUPLICATE_USER_STATUS("이미 사용자 상태가 존재합니다."),

  // Channel
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
  PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다."),

  // Message
  MESSAGE_NOT_FOUND("메세지를 찾을 수 없습니다."),
  AUTHOR_NOT_FOUND("작성자를 찾을 수 없습니다."),

  // ReadStatus
  READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),

  // File
  FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
  DUPLICATE_FILE("이미 존재하는 파일입니다."),
  FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다."),
  FILE_DOWNLOAD_FAILED("파일 다운로드에 실패했습니다.");

  private final String message;

}
