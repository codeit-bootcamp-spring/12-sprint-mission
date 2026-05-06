package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest dto);

}
