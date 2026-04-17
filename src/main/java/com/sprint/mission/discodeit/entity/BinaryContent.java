package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class BinaryContent implements Serializable {
    private final UUID id;
    private final Instant createdAt;
    private final String filePath;

    public BinaryContent(UUID id, String filePath) {
        this.id = id;
        this.createdAt = Instant.now();
        this.filePath = filePath;
    }
}
