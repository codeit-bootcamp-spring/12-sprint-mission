package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Notice {
    private UUID id;
    private String title;
    private String content;
    private boolean isPinned;
    private Long createdAt;
    private Long updatedAt;

    public Notice(String title, String content, boolean isPinned) {
        id = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();

    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return title; }
    public boolean getIsPinned() { return isPinned; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    public void update(String title, String content, boolean isPinned){
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isPinned=" + isPinned +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
