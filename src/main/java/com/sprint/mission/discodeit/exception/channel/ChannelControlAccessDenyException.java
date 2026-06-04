package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelControlAccessDenyException extends ChannelException {
    public ChannelControlAccessDenyException() {
        super(ErrorCode.CHANNEL_ACCESS_DENIED);
    }

    public ChannelControlAccessDenyException(Throwable cause) {
        super(ErrorCode.CHANNEL_ACCESS_DENIED, cause);
    }
}
