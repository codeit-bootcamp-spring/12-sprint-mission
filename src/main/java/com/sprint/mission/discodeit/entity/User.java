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
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;

    private String username;
    private String email;
    private String password;


    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId){
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

        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
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
                "profileId: " + profileId + "\n" +
                "username: " + username + "\n" +
                "email: " + email + "\n" +
                "password: " + password + "\n" +
                "createdAt: " + createdAtStr + "\n" +
                "updatedAt: " + updatedAtStr + "\n" +
                "\n";
    }
}