package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadStatusCreateDTO {
    private UUID userId;
    private UUID channelId;
}
