package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable,Comparable<User> {
    private final UUID id;
    private String name;
    private String email;
    private String nickname;
    private String password;
    private final Long createdAt;
    private Long updatedAt;

    private static final long serialVersionUID = 1L;

    public User(String name, String email, String nickname, String password) {
        id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void update(User user){
        boolean isUpdated = false;

        if(user.getName() != null){
            this.name = user.getName();
            isUpdated = true;
        }
        if(user.getNickname() != null){
            this.nickname = user.getNickname();
            isUpdated = true;
        }
        if(user.getEmail() != null){
            this.email = user.getEmail();
            isUpdated = true;
        }
        if(user.getPassword() != null){
            this.password = user.getPassword();
            isUpdated = true;
        }

        if(isUpdated){
            this.updatedAt = System.currentTimeMillis();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'+"\n";
    }

    @Override
    public int compareTo(User o) {
        return o.name.compareTo(name);
    }
}
