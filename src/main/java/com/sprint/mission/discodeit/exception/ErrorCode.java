package com.sprint.mission.discodeit.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  //User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User with id %s not found"),
  USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "User with email %s not found"),
  USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "User with email %s already exists"),
  USER_USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "User with username %s already exists"),
  USER_ID_AND_MESSAGE_ID_MUST_NOT_BE_NULL(HttpStatus.BAD_REQUEST,
      "userId and messageId shouldn't be both assigned null"),
  WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "Wrong password"),
  INVALID_USER_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password"),

  //Channel
  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel with id %s not found"),
  PRIVATE_CHANNEL_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "Private channel cannot be updated"),
  PRIVATE_CHANNEL_READ_STATUS_FORBIDDEN(HttpStatus.BAD_REQUEST,
      "Private channel ReadStatus can only be created internally"),
  //Message
  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Message with id %s not found"),
  MESSAGE_CONTENT_LIMIT_EXCEEDED(HttpStatus.CONFLICT, " Message content limit exceeded"),

  // Userstatus
  USERSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "UserStatus with id %s not found"),
  USERSTATUS_ALREADY_EXIST(HttpStatus.CONFLICT, "UserStatus with id %s already exists"),
  USERSTATUS_USER_ID_ALREADY_EXIST(HttpStatus.CONFLICT, "UserStatus with userId %s already exists"),

  //Binarycontent
  BINARYCONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BinaryContent with id %s not found"),

  //ReadStatus
  READSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "ReadStatus with id %s not found"),
  READSTATUS_ALREADY_EXIST(HttpStatus.CONFLICT,
      "ReadStatus with userId %s and channelId %s already exists"),
  // File Management
  FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing file "),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File with id %s not found"),
  FILE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "Invalid file format"),
  ;

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

