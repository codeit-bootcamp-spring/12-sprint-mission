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

    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastReadAt;


    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.lastReadAt = lastReadAt;
    }
    public void update(Instant newLastReadAt){
        this.lastReadAt = newLastReadAt;
        this.updatedAt = Instant.now();
    }

}
