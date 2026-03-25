package com.sprint.mission.discodeit.entity;

import java.util.StringJoiner;
import java.util.UUID;

public class Message {

    private final UUID id;
    private final UUID channelId;
    private final UUID serverId;
    private final UUID memberId;

    private final Long createdAt;
    private Long updatedAt;

    private String content;

    private boolean isEdited;

    public Message(UUID channelId, UUID serverId, UUID memberId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.isEdited = false;

        this.channelId = channelId;
        this.serverId = serverId;
        this.memberId = memberId;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getServerId() {
        return serverId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void update(String newContent) {
        this.content = newContent;
        this.isEdited = true;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Message.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("channelId=" + channelId)
                .add("serverId=" + serverId)
                .add("memberId=" + memberId)
                .add("createdAt=" + createdAt)
                .add("updatedAt=" + updatedAt)
                .add("content='" + content + "'")
                .add("isEdited=" + isEdited)
                .toString();
    }
}
