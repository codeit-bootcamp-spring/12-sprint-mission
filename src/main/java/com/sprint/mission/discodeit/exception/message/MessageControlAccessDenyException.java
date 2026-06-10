package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageControlAccessDenyException extends MessageException {
    public MessageControlAccessDenyException(ErrorCode errorCode) {
        super(errorCode); // message 수정/삭제 권한 없음.
    }

    public MessageControlAccessDenyException(ErrorCode errorCode,Throwable cause) {
        super(errorCode,cause); // message 수정/삭제 권한 없음.
    }
}
