package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistException extends DiscodeitException {
	public UserAlreadyExistException() {
		super(ErrorCode.DUPLICATE_USER);
	}

	public UserAlreadyExistException(Throwable cause) {
		super(ErrorCode.DUPLICATE_USER, cause);
	}
}
