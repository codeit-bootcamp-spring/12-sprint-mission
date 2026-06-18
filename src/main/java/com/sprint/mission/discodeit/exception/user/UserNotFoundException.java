package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends DiscodeitException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}

	public UserNotFoundException(Throwable cause) {
		super(ErrorCode.USER_NOT_FOUND, cause);
	}
}
