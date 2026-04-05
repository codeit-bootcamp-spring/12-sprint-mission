package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private UUID id;
    private UUID channelId;   // 이 메시지가 어느 방에 써졌는지
    private UUID authorId;    // 이 메시지를 누가 썼는지 (유저 ID)
    private String content;   // 진짜 메시지 내용
    private Long createdAt;
    private Long updatedAt;

    public Message(UUID channelId, UUID authorId, String content) {
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getter 들
    public UUID getId() { return id; }
    public UUID getChannelId() { return channelId; }
    public UUID getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    // Update 메서드 (메시지 수정할 때)
    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}