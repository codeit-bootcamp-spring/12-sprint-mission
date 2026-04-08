package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    // 사용자 이름, 이메일
    private String username;
    private String email;

    public User(String username, String email) {
        super();
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void update(String username, String email) {
        this.username = username;
        this.email = email;
        touch();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
