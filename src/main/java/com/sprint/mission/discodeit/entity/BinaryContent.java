package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String fileName;
    private String contentType;
    private Instant createdAt;

    public BinaryContent(String fileName, String contentType) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.contentType = contentType;
        this.createdAt = Instant.now();
    }

    @Override
    public String toString() {
        return "BinaryContent {" +
                "\n id          = " + id +
                "\n fileName    = " + fileName +
                "\n contentType = " + contentType +
                "\n createdAt   = " + createdAt +
                "\n}\n";
    }
}
