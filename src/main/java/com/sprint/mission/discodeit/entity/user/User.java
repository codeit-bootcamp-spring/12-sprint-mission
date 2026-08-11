package com.sprint.mission.discodeit.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter @ToString(callSuper = true) @SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;


    public void update(String username, String email, String password, BinaryContent profile) {
        if(username != null) {
            this.username = username;
        }

        if(email != null) {
            this.email = email;
        }

        if(password != null) {
            this.password = password;
        }

        if(profile != null) {
            this.profile = profile;
        }
    }

    public void updateRole(Role role) {
        if (role != null) {
            this.role = role;
        }
    }

    public void clearProfile() {
        this.profile = null;
    }

}
