package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 디폴트 구현체(org.springframework.security.core.userdetails.User)를 대체하는 UserDetails.
 *
 * <p>Principal에 UserDto를 그대로 담아 두면 Controller에서 {@code @AuthenticationPrincipal}로
 * 사용자 정보를 바로 꺼내 쓸 수 있다.
 */
@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }
}
