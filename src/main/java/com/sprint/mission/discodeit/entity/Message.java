package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable, Comparable<Message> {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private UUID channelId;
    private String content;
    private List<UUID> attachmentIds;
    private Instant createdAt;
    private Instant updatedAt;

    public Message(UUID userId, UUID channelId, String content, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = attachmentIds;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Message {" +
                "\n id        = " + id +
                "\n userId    = " + userId +
                "\n channelId = " + channelId +
                "\n content   = " + content +
                "\n createdAt = " + createdAt +
                "\n updatedAt = " + updatedAt +
                "\n}\n";
    }

    @Override
    public int compareTo(Message o) {
        return this.createdAt.compareTo(o.createdAt);
    }
}
