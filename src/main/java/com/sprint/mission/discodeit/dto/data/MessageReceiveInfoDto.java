package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record MessageReceiveInfoDto(
        UUID id,
        UUID channelId,
        UUID userId,
        Boolean read,
        Boolean alarmOn
) {}
