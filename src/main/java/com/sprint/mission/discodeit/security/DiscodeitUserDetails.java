package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

  // hasRole('ADMIN') 형태로 검사하므로 ROLE_ 접두사를 붙여준다
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
  }

  // @PreAuthorize SpEL에서 principal.userId로 바로 참조하기 위한 편의 메소드
  public UUID getUserId() {
    return userDto.id();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  // SessionRegistry는 in-memory Map의 key로 Principal을 사용하므로,
  // 같은 사용자의 세션을 동일하게 인식하려면 equals/hashCode를 재정의해야 한다
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DiscodeitUserDetails other)) {
      return false;
    }
    return Objects.equals(getUserId(), other.getUserId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUserId());
  }
}
