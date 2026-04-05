package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private String content;
    private User user;
    private Channel channel;
    private Long createdAt;
    private Long updatedAt;

    public Message(String content, User user, Channel channel) {
        this.id = UUID.randomUUID();
        this.content = content; // 메세지
        this.user = user;
        this.channel = channel;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + user +
                ", channel=" + channel +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
