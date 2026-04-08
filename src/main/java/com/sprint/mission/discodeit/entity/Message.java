package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    // 내용, 작성자, 채널
    private String content;
    private UUID userId;
    private UUID channleId;

    public Message(String content, UUID userId, UUID channleId) {
        super();
        this.content = content;
        this.userId = userId;
        this.channleId = channleId;
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannleId() {
        return channleId;
    }

    public void update(String content, UUID userId, UUID channelId) {
        this.content = content;
        this.userId = userId;
        this.channleId = channleId;
        touch();
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", userId='" + userId + '\'' +
                ", channleId='" + channleId + '\'' +
                '}';
    }
}
