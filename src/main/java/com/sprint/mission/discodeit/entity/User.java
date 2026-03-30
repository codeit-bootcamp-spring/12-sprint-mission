package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private Long createdAt;
    private Long updatedAt;

    public User(String username, String email, String password, String nickname) {
        id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }


    public void update(String username, String email, String password, String nickname){
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ct = sdf.format(new Date(createdAt));
        String ut = sdf.format(new Date(updatedAt));

        return "User{" +
                "id=" + id +
                ", 이름='" + username + '\'' +
                ", 이메일='" + email + '\'' +
                ", 비밀번호='" + password + '\'' +
                ", 닉네임='" + nickname + '\'' +
                ", 가입 시간=" + ct +
                ", 수정 시간=" + ut +
                '}';
    }
}
