package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.LoginRequest;
import com.sprint.mission.discodeit.dto.data.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}