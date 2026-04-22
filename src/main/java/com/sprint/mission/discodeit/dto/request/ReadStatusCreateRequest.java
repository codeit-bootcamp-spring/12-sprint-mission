package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusCreateRequest {
    private final UUID userId;
    private final UUID channelId;
}
