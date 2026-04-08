package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private String nickname;

    public User(String username, String password, String email, String nickname) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public void update(String username, String password, String email, String nickname) {
        super.touch();
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }


    @Override
    public String toString() {
        return "User{" +
                super.toString() +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}