package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private String channelName; // 채널명
    private ChannelType channelType; // 채널타입(텍스트, 음성)
    private String description; // 채널 설명
    private User owner; // 방장
    private List<User> members; // 채널 멤버
    private Long createdAt;
    private Long updatedAt;

    public Channel(String channelName, ChannelType channelType, String description, User owner) {
        this.id = UUID.randomUUID();
        this.channelName = channelName;
        this.channelType = channelType;
        this.description = description;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.members.add(owner);
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public String getDescription() {
        return description;
    }

    public User getOwner() {
        return owner;
    }

    public List<User> getMembers() {
        return members;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // 수정 update 함수
    public void update(String channelName, ChannelType channelType, String description){
        this.channelName = channelName;
        this.channelType = channelType;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", channelName='" + channelName + '\'' +
                ", channelType=" + channelType +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", members=" + members +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
