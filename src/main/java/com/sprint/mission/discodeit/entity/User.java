package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
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

    private final Long createdAt;
    private Long updatedAt;

    public User(String userName, String password, String email, String nickName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.userName = userName;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
    }

    public void update(String newUserName, String newPassword, String newEmail, String newNickName) {
        this.userName = newUserName;
        this.password = newPassword;
        this.email = newEmail;
        this.updatedAt = System.currentTimeMillis();
        this.nickName = newNickName;
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
                .toString();
    }
}
