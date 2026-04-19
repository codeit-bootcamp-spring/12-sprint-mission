package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {

    private UUID id;

    private UUID userId;
    private UUID channelId;

    private Instant lastReadAt;

    private Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;

        this.lastReadAt = Instant.now();
        this.createdAt = this.lastReadAt;
        this.updatedAt = this.lastReadAt;
    }

    public void updateLastRead() {
        this.lastReadAt = Instant.now();
        this.updatedAt = this.lastReadAt;
    }
}