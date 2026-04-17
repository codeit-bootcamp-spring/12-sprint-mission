package com.sprint.mission.discodeit.entity;

import jakarta.websocket.Decoder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class BinaryContent {
    private final String id;
    private final byte[] content;
    private final String fileName;
    private final String contentType;
    private final Instant createdAt;

    private final String userId;
    private final String messageId;

    public BinaryContent(String id, byte[] content, String fileName, String contentType, Instant createdAt,String userId,String messageId) {
        this.id = id;
        this.content = content;
        this.fileName = fileName;
        this.contentType = contentType;
        this.createdAt = createdAt;
        this.userId = userId;
        this.messageId = messageId;
    }
}

