package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageReceiveInfo {

    private UUID id;
    private UUID channelId;
    private UUID userId;
    private Boolean read;
    private Boolean alarmOn;


    public MessageReceiveInfo(UUID userId, UUID channelId, Boolean read, Boolean aBoolean) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.read = read;
        this.alarmOn = alarmOn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getAlarmOn() {
        return alarmOn;
    }

    public void setAlarmOn(Boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean isRead() {
        return read;
    }

    public Boolean isAlarmOn() {
        return alarmOn;
    }
}

