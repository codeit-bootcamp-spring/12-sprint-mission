package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  public User login(LoginRequest loginRequest) {
    if (loginRequest.username() == null || loginRequest.username().isBlank() ||
        loginRequest.password() == null || loginRequest.password().isBlank()) {
      throw new IllegalArgumentException("Username and newPassword must not be null");
    }

    User user = userRepository.findByUsername(loginRequest.username())
        .orElseThrow(() -> new IllegalArgumentException(
            "Username " + loginRequest.username() + " not found"));

    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("Wrong newPassword");
    }

    return user;
  }

}
