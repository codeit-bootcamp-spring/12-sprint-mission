package com.sprint.mission.discodeit.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrivateChannelCreateRequest {
    private final List<UUID> participantIds;
}
