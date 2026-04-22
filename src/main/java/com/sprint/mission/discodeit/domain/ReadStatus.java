package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastReadAt;

    private final UUID userId;
    private final UUID channelId;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.lastReadAt = lastReadAt;

        this.userId = userId;
        this.channelId = channelId;

    }

    public void update(Instant newLastReadAt){
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", lastReadAt=" + lastReadAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
