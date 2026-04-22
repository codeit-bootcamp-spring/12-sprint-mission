package com.sprint.mission.discodeit.entity.channel;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import lombok.Getter;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private ChannelType type;
    private String name;
    private String description;
    private final boolean isPrivate;

    public Channel(ChannelType type, String name, String description, boolean isPrivate) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.type = type;
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
    }

    public void update(ChannelType type, String name, String description) {
        if (isPrivate) {
            throw new IllegalArgumentException("수정 불가");
        }

        this.type = type;
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }
}