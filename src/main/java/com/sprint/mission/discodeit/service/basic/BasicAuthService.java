package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.WrongPasswordException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public UserResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.email()).orElseThrow(
        () -> UserNotFoundException.withEmail(request.email())
    );

    if (!user.getPassword().equals(request.password())) {
      throw new WrongPasswordException(request.email());
    }

    return UserResponse.from(user);
  }
}
