package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    private final UUID channelId;
    private ChannelType channelType;
    private String channelName;
    private String description;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(ChannelType channelType, String channelName, String description) {
        channelId = UUID.randomUUID();
        this.channelType = channelType;
        this.channelName = channelName;
        this.description = description;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getChannelId() {
        return channelId;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", channelType=" + channelType +
                ", channelName='" + channelName + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}