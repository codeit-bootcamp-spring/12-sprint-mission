package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.StringJoiner;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String name;
    private final ChannelType type;
    private boolean isPrivate;

    public Channel(String name, ChannelType type, boolean isPrivate) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.name = name;
        this.type = type;
        this.isPrivate = isPrivate;
    }

    public void update(String newName, boolean newPrivate) {
        this.name = newName;
        this.updatedAt = Instant.now();
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

