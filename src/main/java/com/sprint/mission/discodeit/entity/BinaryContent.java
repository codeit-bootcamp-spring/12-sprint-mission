package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final String filePath;

    public BinaryContent(UUID id, String filePath) {
        this.id = id;
        this.createdAt = Instant.now();
        this.filePath = filePath;
    }
}
