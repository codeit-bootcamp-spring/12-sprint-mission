package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String username;
    private String email;
    private transient String password;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();

        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(String newUsername, String newEmail, String newPassword){
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().toEpochMilli();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtStr = formatter.format(Instant.ofEpochMilli(createdAt));
        String updatedAtStr = formatter.format(Instant.ofEpochMilli(updatedAt));

        return "id: " + id + "\n" +
                "username: " + username + "\n" +
                "email: " + email + "\n" +
                "password: " + password + "\n" +
                "createdAt: " + createdAtStr + "\n" +
                "updatedAt: " + updatedAtStr + "\n" +
                "\n";
    }
}