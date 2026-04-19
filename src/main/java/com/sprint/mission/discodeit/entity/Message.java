package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID channelId;
    private final UUID memberId;

    private final Instant createdAt;
    private Instant updatedAt;

    private String content;
    private List<UUID> attachmentIds;

    private boolean isEdited;

    public Message(UUID channelId, UUID memberId, String content, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.isEdited = false;

        this.channelId = channelId;
        this.memberId = memberId;
        this.content = content;
        this.attachmentIds = attachmentIds;
    }

    public void update(String newContent, List<UUID> attachmentIds) {
        this.content = newContent;
        this.isEdited = true;
        this.updatedAt = Instant.now();
        this.attachmentIds = attachmentIds;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Message.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("channelId=" + channelId)
                .add("memberId=" + memberId)
                .add("createdAt=" + createdAt)
                .add("updatedAt=" + updatedAt)
                .add("content='" + content + "'")
                .add("isEdited=" + isEdited)
                .add("attachmentIds=" + attachmentIds)
                .toString();
    }
}
