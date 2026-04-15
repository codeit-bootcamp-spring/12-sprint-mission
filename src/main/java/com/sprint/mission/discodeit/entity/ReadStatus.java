package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private UUID id;
    private UUID userId;
    private UUID channelId;

    private Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID id, UUID userId, UUID channelId) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
    }

    public void update(){
        this.updatedAt = Instant.now();
    }
}
