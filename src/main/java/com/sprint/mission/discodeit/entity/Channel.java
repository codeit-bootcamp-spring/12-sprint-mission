package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private final UUID id;
    private final UUID userId;
    private String title;
    private String description;
    private final ChannelType type;
    private final Instant createdAt;
    private Instant updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;

    public Channel(ChannelType type, String title, UUID userId, String description) {
        id = UUID.randomUUID();
        this.title = title;
        this.userId = userId;
        this.description = description;
        this.type = type;
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    public void update(String title, String description){
        boolean isUpdated = false;

        if(title != null){
            this.title = title;
            isUpdated = true;
        }
        if(description != null){
            this.description = description;
            isUpdated = true;
        }

        if(isUpdated){
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", user=" + userId +
                ", title='" + title + '\'' +
                ", category='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'+"\n";
    }
}
