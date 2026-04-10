package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> memberIds,
        UUID userId
) {

}
