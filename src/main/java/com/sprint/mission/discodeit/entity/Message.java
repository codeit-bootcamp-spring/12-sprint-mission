package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String content;
    private final UUID channelId;
    private final UUID authorId;

    private List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;

    }

    public void update(String newContent){
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtStr = formatter.format(createdAt);
        String updatedAtStr = formatter.format(updatedAt);

        return "id: " + id + "\n" +
                "content: " + content + "\n" +
                "createdAt: " + createdAtStr + "\n" +
                "updatedAt: " + updatedAtStr + "\n" +
                "channelId: " + channelId + "\n" +
                "authorId: " + authorId + "\n";
    }
}
