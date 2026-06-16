package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.exception.InvalidCredentialsException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserService userService;

  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();

    return userRepository.findByUsername(username)
        .filter(user -> user.getPassword().equals(loginRequest.password()))
        .map(user -> userService.find(user.getId()))
        .orElseThrow(() -> new InvalidCredentialsException(username));
  }
}
