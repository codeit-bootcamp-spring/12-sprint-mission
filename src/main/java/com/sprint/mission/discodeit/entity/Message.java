package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private final UUID id;
    private final UUID channelId;
    private final UUID userId;
    private String title;
    private String content;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<UUID> attachmentIds;

    @Serial
    private static final long serialVersionUID = 1L;

    public Message(UUID channelId, UUID userId, String title, String content, List<UUID> attachmentIds) {
        id = UUID.randomUUID();
        this.channelId = channelId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.attachmentIds = attachmentIds == null ? new ArrayList<>() : attachmentIds;
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    public void addContents(BinaryContent binaryContent){
        attachmentIds.add(binaryContent.getId());
    }

    public void update(String title, String content){
        boolean isUpdated = false;

        if(title != null){
            this.title = title;
            isUpdated = true;
        }
        if(content != null){
            this.content = content;
            isUpdated = true;
        }

        if(isUpdated){
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channel=" + channelId +
                ", user=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'+"\n";
    }
}
