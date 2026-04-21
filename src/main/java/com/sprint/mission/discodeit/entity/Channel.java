package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private UUID id;
    private String name;
    private String description;

    private List<User> users;

    private Instant createdAt;
    private Instant updatedAt;

    public Channel(String name, String description) {
        this.id = UUID.randomUUID();

        this.name = name;
        this.description = description;

        this.users = new ArrayList<>();

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }


    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime created = LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());
        LocalDateTime updated = LocalDateTime.ofInstant(updatedAt, ZoneId.systemDefault());

        return "[Channel]" +
                "\n- ID: " + id +
                "\n- Name: " + name +
                "\n- Description: " + description +
                "\n- Users: " + users +
                "\n- CreatedAt: " + created.format(formatter) +
                "\n- UpdatedAt: " + updated.format(formatter) +
                "\n";
    }
}