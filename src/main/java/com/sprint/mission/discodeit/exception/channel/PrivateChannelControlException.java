package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelControlException extends ChannelException {
    public PrivateChannelControlException() {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
    }
    public PrivateChannelControlException(Throwable cause) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE, cause);
    }
}
