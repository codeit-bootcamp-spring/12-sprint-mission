package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable,Comparable<Message> {
    private final UUID id;
    private final Channel ch;
    private final User author;
    private String title;
    private String content;
    private final Long createdAt;
    private Long updatedAt;

    private static final long serialVersionUID = 1L;

    public Message(Channel ch, User author, String title, String content) {
        id = UUID.randomUUID();
        this.ch = ch;
        this.author = author;
        this.title = title;
        this.content = content;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
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

    public void update(Message message){
        boolean isUpdated = false;

        if(message.getTitle() != null){
            this.title = message.getTitle();
            isUpdated = true;
        }
        if(message.getContent() != null){
            this.content = message.getContent();
            isUpdated = true;
        }

        if(isUpdated){
            this.updatedAt = System.currentTimeMillis();
        }
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
                '}'+"\n";
    }

    @Override
    public int compareTo(Message o) {
        return o.ch.compareTo(ch);
    }
}
