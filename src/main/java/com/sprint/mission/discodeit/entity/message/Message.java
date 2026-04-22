package com.sprint.mission.discodeit.entity.message;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID authorId;
    private final UUID channelId;
    private String content;
    private List<UUID> attachmentIds;

    public Message(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
        id = UUID.randomUUID();
        createdAt = Instant.now();
        updatedAt = createdAt;

        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = attachmentIds;
    }

    public void update(String content, List<UUID> attachmentIds) {
        this.content = content;
        this.attachmentIds = attachmentIds;

        this.updatedAt = Instant.now();
    }
}
