package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto login(LoginRequest loginRequest) {
    log.debug("로그인 시도 : ID = {}", loginRequest.username());
    String username = loginRequest.username();
    String password = loginRequest.password();

    User user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> {
              log.warn("잘못된 사용자 이름 입력 : ID = {}", username);
              return UserNotFoundException.withUsername(username);
            });

    if (!user.getPassword().equals(password)) {
      log.warn("잘못된 비밀번호 입력 : ID = {}", username);
      throw InvalidCredentialsException.wrongPassword(password);
    }
    log.info("로그인 성공 : ID = {}", username);
    return userMapper.toDto(user);
  }
}
