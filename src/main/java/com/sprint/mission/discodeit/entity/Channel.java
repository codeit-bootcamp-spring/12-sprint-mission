package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String Name;
    private String description;
    private ChannelCategory category;
    private Long createdAt;
    private Long updatedAt;

    public Channel(ChannelCategory category, String name, String description) {
        this.id = UUID.randomUUID();
        this.category = category;
        this.Name = name;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() { return id; }
    public String getName() { return Name; }
    public String getDescription() { return description; }
    public ChannelCategory getCategory() { return category; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.Name)) {
            this.Name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAt = System.currentTimeMillis();
        }
    }
}



