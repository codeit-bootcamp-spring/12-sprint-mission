package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.StringJoiner;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String userName;
    private String password;
    private String email;
    private String nickName;
    private UUID profileId;

    private final Instant createdAt;
    private Instant updatedAt;

    public User(String userName, String password, String email, String nickName, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.userName = userName;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.profileId = profileId;
    }

    public void update(String newUserName, String newPassword, String newEmail, String newNickName, UUID profileId) {
        this.userName = newUserName;
        this.password = newPassword;
        this.email = newEmail;
        this.updatedAt = Instant.now();
        this.nickName = newNickName;
        this.profileId = profileId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("userName='" + userName + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("nickName='" + nickName + "'")
                .add("createdAt=" + createdAt)
                .add("updatedAt=" + updatedAt)
                .add("profileId=" + profileId)
                .toString();
    }
}
