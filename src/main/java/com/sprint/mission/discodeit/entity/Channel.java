package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum ChannelType {
        TEXT, VOICE, FORUM
    }

    private final UUID id;

    private final Long createdAt;
    private Long updatedAt;

    private String name;

    private final ChannelType type;
    private boolean isPrivate;

    public Channel(String name, ChannelType type, boolean isPrivate) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.name = name;
        this.type = type;
        this.isPrivate = isPrivate;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public ChannelType getType() {
        return type;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void update(String newName, boolean newPrivate) {
        this.name = newName;
        this.updatedAt = System.currentTimeMillis();
        this.isPrivate = newPrivate;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Channel.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("createdAt=" + createdAt)
                .add("updatedAt=" + updatedAt)
                .add("name='" + name + "'")
                .add("type=" + type)
                .add("isPrivate=" + isPrivate)
                .toString();
    }
}

