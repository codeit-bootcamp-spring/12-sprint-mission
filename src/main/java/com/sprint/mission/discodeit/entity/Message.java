package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity{

    private UUID userId;
    private UUID channelId;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void update(String content){
        this.content = this.content;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return "Message{" +
                "userId=" + userId +
                ", channelId=" + channelId +
                ", content='" + content + '\'' +
                '}';
    }
}
