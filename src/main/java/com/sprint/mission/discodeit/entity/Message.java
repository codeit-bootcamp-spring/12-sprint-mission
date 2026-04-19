package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private UUID id;
    private String content;
    private User user;
    private Channel channel;
    private List<UUID> binaryContentIds;
    private Instant createdAt;
    private Instant updatedAt;

    public Message(String content, User user, Channel channel, List<UUID> binaryContentIds) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.user = user;
        this.channel = channel;
        this.binaryContentIds = binaryContentIds != null ? binaryContentIds : new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime created = LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());
        LocalDateTime updated = LocalDateTime.ofInstant(updatedAt, ZoneId.systemDefault());

        return "[Message]" +
                "\n- ID: " + id +
                "\n- Content: " + content +
                "\n- User: " + user.getUsername() +
                "\n- Channel: " + channel.getName() +
                "\n- BinaryContentIds: " + binaryContentIds +
                "\n- CreatedAt: " + created.format(formatter) +
                "\n- UpdatedAt: " + updated.format(formatter) +
                "\n";
    }
}