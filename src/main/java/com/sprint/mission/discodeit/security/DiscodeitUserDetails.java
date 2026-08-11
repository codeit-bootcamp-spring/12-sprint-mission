package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserResponse userResponse;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + userResponse.role().name()
                )
        );
    }

    @Override
    public String getUsername() {
        return userResponse.username();
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }

        if (!(object instanceof DiscodeitUserDetails other)) {
            return false;
        }

        return userResponse.id().equals(other.userResponse.id());
    }

    @Override
    public int hashCode() {
        return userResponse.id().hashCode();
    }
}
