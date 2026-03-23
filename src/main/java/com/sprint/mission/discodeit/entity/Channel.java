package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {
    private final Set<UUID> userList;
    private UUID channelOwnerId;
    ChannelType type;
    String name;
    boolean isPrivate;

    public Channel(UUID channelOwnerId, ChannelType type, String name, boolean isPrivate) {
        super();
        this.userList = new HashSet<>();
        this.channelOwnerId = channelOwnerId;
        this.type = type;
        this.name = name;
        this.isPrivate = isPrivate;

        userList.add(channelOwnerId);
    }

    public List<UUID> getUserList() {
        return userList.stream().toList();
    }

    public UUID getChannelOwnerId() {
        return channelOwnerId;
    }

    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void addUser(UUID id) {
        super.touch();
        userList.add(id);
    }

    public void update(UUID channelOwnerId, ChannelType type, String name, boolean isPrivate) {
        super.touch();
        this.channelOwnerId = channelOwnerId;
        this.type = type;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    @Override
    public String toString() {
        return "Channel{" +
                super.toString() +
                ", userCount=" + userList.size() +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", isPrivate=" + isPrivate +
                '}';
    }
}

