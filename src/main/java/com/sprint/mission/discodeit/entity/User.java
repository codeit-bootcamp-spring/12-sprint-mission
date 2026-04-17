package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private final UUID id;
    private UUID profileId;
    private  String name;
    private final String email;
    private final String nickname;
    private String password;
    private final Instant createdAt;
    private Instant updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;

    public User(BinaryContent profile, String name, String email, String nickname, String password) {
        this.id = UUID.randomUUID();
        this.profileId = (profile != null ? profile.getId(): null);
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    public void update(String name,
                       UUID profileId,
                       String password ){
        boolean isUpdated = false;

        if(profileId != null){
            this.profileId = profileId;
            isUpdated = true;
        }

        if(name != null){
            this.name = name;
            isUpdated = true;
        }

        if(password != null){
            this.password = password;
            isUpdated = true;
        }

        if(isUpdated){
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'+"\n";
    }
}
