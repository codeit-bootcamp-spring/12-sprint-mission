package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable, Comparable<Channel> {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private ChannelType type;
    private String title;
    private Long createdAt;
    private Long updatedAt;

    public Channel(ChannelType type, String title) {
        id = UUID.randomUUID();
        this.type = type;
        this.title = title;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public ChannelType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String title) {
        this.title = title;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel {" +
                "\n id        = " + id +
                "\n type      = " + type +
                "\n title     = " + title +
                "\n createdAt = " + createdAt +
                "\n updatedAt = " + updatedAt +
                "\n}\n";
    }

    @Override
    public int compareTo(Channel o) {
        return this.title.compareTo(o.title);
    }
}