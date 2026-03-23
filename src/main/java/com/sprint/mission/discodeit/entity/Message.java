package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Channel ch;
    private final User author;
    private String title;
    private String content;
    private final long createdAt;
    private long updatedAt;

    public Message(Channel ch, User author, String title, String content) {
        id = UUID.randomUUID();
        this.ch = ch;
        this.author = author;
        this.title = title;
        this.content = content;
        createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Channel getCh() {
        return ch;
    }

    public User getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateTitle(String title) {
        this.title = title;
        updatedAt = System.currentTimeMillis();
    }

    public void updateContent(String content) {
        this.content = content;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", ch=" + ch.getTitle() +
                ", author=" + author.getName() +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
