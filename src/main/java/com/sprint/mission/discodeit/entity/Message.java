package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private UUID authorId;
    private UUID channelId;
    private String content;

    public Message(UUID authorId, UUID channelId, String content) {
        super();
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public void update(UUID authorId, UUID channelId, String content) {
        super.touch();
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                super.toString() +
                ", content='" + content + '\'' +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                '}';
    }
}
