package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        this.content = content;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime created = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault());
        LocalDateTime updated = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault());

        return "[Message]" +
                "\n- ID: " + id +
                "\n- Content: " + content +
                "\n- User: " + user.getUsername() +
                "\n- Channel: " + channel.getName() +
                "\n- CreatedAt: " + created.format(formatter) +
                "\n- UpdatedAt: " + updated.format(formatter) +
                "\n";
    }
}