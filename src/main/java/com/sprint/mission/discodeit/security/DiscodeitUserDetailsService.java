package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    return new DiscodeitUserDetails( // SpringSecurity 에 반환
        userMapper.toDto(user, false),
        user.getPassword()
    );
  }

}
