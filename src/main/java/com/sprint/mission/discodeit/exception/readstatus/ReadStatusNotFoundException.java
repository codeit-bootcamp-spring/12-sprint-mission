package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;


import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {
    public ReadStatusNotFoundException(UUID id) {
        super(ErrorCode.READSTATUS_NOT_FOUND,
                ErrorCode.READSTATUS_NOT_FOUND.format(id),
                Map.of("readStatusId", id));
    }
}