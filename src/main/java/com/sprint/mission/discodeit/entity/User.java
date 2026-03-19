package com.sprint.mission.discodeit.entity;

import java.util.StringJoiner;
import java.util.UUID;

public class User {
    private UUID id;
    private String userName;
    private String password;
    private String email;
    private String nickName;

    private Long createdAt;
    private Long updatedAt;

    public User(UUID id, String userName, String password, String email, String nickName, Long createdAt, Long updatedAt) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
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
