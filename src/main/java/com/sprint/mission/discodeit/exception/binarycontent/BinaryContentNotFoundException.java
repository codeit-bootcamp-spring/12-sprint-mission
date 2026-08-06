package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(UUID id) {
        super(ErrorCode.BINARYCONTENT_NOT_FOUND,
                ErrorCode.BINARYCONTENT_NOT_FOUND.format(id),
                Map.of("binaryContentId", id));
    }
}