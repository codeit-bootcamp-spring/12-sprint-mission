package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(
        new SimpleGrantedAuthority("ROLE_" + userDto.role().name())
    );
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  // 프로필, 이름 등의 값이 바뀌어도 같은 사용자로 인식되어야 함
  // -> 같은 사용자인지 판단할 때 UserDto 전체 값이 아니라 사용자 ID만 비교한다.
  @Override
  public boolean equals(Object o) {
    return o instanceof DiscodeitUserDetails that
        && userDto.id().equals(that.userDto.id());
  }

  // equals()와 같은 기준인 사용자 ID로 hashCode를 생성
  @Override
  public int hashCode() {
    return userDto.id().hashCode();
  }
}
