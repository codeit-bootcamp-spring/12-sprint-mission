package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.dto.Auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

public interface AuthService {
    UserResponse login(AuthLoginRequest dto);

}
