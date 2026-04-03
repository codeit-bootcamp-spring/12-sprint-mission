package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private UUID id;
    private long createdAt;
    private long updatedAt;

    private String username;
    private String email;
    private String password;
//    private String nickname;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.username = username;
        this.email = email;
        this.password = password;
//        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getNickname() {
//        return nickname;
//    }
//
//    public void setNickname(String nickname) {
//        this.nickname = nickname;
//    }

    public void update(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
//        this.nickname = nickname;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User{" +
                " username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
//                ", nickname='" + nickname + '\'' + ", " +
                "id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                '}' + "\n";
    }
}