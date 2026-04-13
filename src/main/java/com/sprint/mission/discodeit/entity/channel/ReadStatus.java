package com.sprint.mission.discodeit.entity.channel;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private final UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        id = UUID.randomUUID();
        createdAt = Instant.now();
        updatedAt = createdAt;

        this.userId = userId;
        this.channelId = channelId;
    }

    public void update() {
        this.updatedAt = Instant.now();
    }
}
