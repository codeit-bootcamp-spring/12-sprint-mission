package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusUpdateDTO {
    private UUID id;
    private Instant accessedAt;
}
