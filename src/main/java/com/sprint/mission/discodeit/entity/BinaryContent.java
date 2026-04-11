package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private final UUID id;
    private final String fileName;
    private final String url;
    private final Instant createdAt;

    public BinaryContent( String fileName, String url) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.url = url;
        this.createdAt = Instant.now();
    }

}

