package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 디폴트 구현체(InMemoryUserDetailsManager)를 대체하는 UserDetailsService.
 *
 * <p>Bean으로 등록해두면 DaoAuthenticationProvider가 자동으로 이 구현체를 사용한다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username));
    log.debug("UserDetails 로드: username={}", username);
    return new DiscodeitUserDetails(userMapper.toDto(user), user.getPassword());
  }
}
