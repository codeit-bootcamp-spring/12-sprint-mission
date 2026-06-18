package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {
	private final Instant timestamp;
	private final ErrorCode errorCode;
	private final Map<String, Object> details;

	public DiscodeitException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		timestamp = Instant.now();
		details = new LinkedHashMap<>();
	}

	public DiscodeitException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
		timestamp = Instant.now();
		details = new LinkedHashMap<>();
	}

	public void addDetail(String key, Object value) {
		this.getDetails().put(key, value);
	}

}
