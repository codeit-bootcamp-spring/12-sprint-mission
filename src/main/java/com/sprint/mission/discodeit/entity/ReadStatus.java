package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private Instant updateReadAt;

    public UUID getId() {
        return Id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public Instant getReadAt() {
        return readAt;
    }

    private UUID Id;
    private UUID userId;
    private UUID channelId;
    private Instant readAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());

    public ReadStatus(UUID ID, UUID userId, UUID channelId, Instant readAt) {
        this.Id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
    }

    public void updateReadAt() {
        this.updateReadAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
    }
}


