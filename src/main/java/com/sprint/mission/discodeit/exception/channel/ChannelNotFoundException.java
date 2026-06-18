package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotFoundException extends DiscodeitException {
	public ChannelNotFoundException() {
		super(ErrorCode.CHANNEL_NOT_FOUND);
	}

	public ChannelNotFoundException(Throwable cause) {
		super(ErrorCode.CHANNEL_NOT_FOUND, cause);
	}
}
