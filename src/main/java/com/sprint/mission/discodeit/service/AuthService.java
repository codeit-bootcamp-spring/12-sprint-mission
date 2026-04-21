package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}
