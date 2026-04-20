package com.sprint.mission.discodeit.dto.data;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
	private int code;
	private String status;
	private String message;

	public ErrorResponse(HttpStatus httpStatus, String message) {
		this.code = httpStatus.value();
		this.status = httpStatus.name();
		this.message = message;
	}
}
