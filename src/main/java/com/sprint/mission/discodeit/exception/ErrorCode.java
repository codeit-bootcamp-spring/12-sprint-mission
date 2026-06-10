package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  //user
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User with id %s not found"),
  USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "User with email %s not found"),
  USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "User with email %s already exists"),
  USER_USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "User with username %s already exists"),
  USER_ID_AND_MESSAGE_ID_MUST_NOT_BE_NULL(HttpStatus.BAD_REQUEST,
      "userId와 messageId는 둘 다 null일 수 없습니다."),
  WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "Wrong password"),
  USERSTATUS_WITH_USERID_NOT_FOUND(HttpStatus.NOT_FOUND, "UserStatus with userId %s not found"),
  USER_DUPLICATE(HttpStatus.CONFLICT, "유저 중복"),

  //channel
  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel with id %s not found"),
  PRIVATE_CHANNEL_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "Private channel cannot be updated"),
  PRIVATE_CHANNEL_READ_STATUS_FORBIDDEN(HttpStatus.BAD_REQUEST,
      "Private channel ReadStatus can only be created internally"),

  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Message with id %s not found"),

  USERSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "UserStatus with id %s not found"),
  USERSTATUS_ALREADY_EXIST(HttpStatus.CONFLICT, "UserStatus with id %s already exists"),
  USERSTATUS_USER_ID_ALREADY_EXIST(HttpStatus.CONFLICT, "UserStatus with userId %s already exists"),

  BINARYCONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BinaryContent with id %s not found"),

  READSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "ReadStatus with id %s not found"),
  READSTATUS_ALREADY_EXIST(HttpStatus.CONFLICT,
      "ReadStatus with userId %s and channelId %s already exists"),

  FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류 발생");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public String format(Object... args) {
    return message.formatted(args);
  }
}
