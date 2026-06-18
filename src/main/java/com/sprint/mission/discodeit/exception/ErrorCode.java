package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

	USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
	DUPLICATE_USER("중복되는 사용자입니다"),
	CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다"),
	PRIVATE_CHANNEL_UPDATE("개인 채널은 업데이트할 수 없습니다");

	private final String message;

	ErrorCode(String message) {
		this.message = message;
	}

}
