package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateUsername(String username){
        this.username = username;
        touch();
    }
    public void updateEmail(String email){
        this.email = email;
        touch();
    }

    public void updatePassword(String password) {
        this.password = password;
        touch();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
