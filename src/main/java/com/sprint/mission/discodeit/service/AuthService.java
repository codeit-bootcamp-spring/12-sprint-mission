package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.data.user.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}