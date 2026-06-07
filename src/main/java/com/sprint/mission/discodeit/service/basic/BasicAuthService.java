package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    log.info("로그인 요청: username={}", username);
    String password = loginRequest.password();

    User user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> {
              log.warn("로그인 실패 - {}: username={}", ErrorCode.USER_NOT_FOUND.getMessage(), username);
              return new UserNotFoundException();
            });
    if (!user.getPassword().equals(loginRequest.password())) {
      log.warn("로그인 실패 - {}: username={}", ErrorCode.INVALID_USER_CREDENTIALS.getMessage(),
          username);
      throw new UserException(ErrorCode.INVALID_USER_CREDENTIALS);
    }
    log.info("로그인 성공: username={}", username);
    return userMapper.toDto(user);
  }
}
