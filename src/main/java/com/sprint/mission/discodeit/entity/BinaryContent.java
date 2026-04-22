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
    private final UUID authorId;
    private final UUID messageId;
    private final UUID userId;
    private final byte[] imageContent;

    public BinaryContent(UUID userId, UUID authorId, UUID messageId, byte[] imageContent) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.authorId = authorId;
        this.messageId = messageId;
        this.imageContent = imageContent;

    }

    public static BinaryContent createProfileImage(UUID userId, byte[] imageContent) {
        return new BinaryContent(userId, null, null, imageContent);
    }

    public static BinaryContent createMessageImage(UUID authorId, UUID messageId, byte[] imageContent) {
        return new BinaryContent(null, authorId, messageId, imageContent);
    }


}
