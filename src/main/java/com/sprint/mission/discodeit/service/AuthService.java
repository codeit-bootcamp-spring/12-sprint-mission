package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

  @Transactional(readOnly = true)
  UserResponse login(LoginRequest loginRequest);
}
