package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginUserRequest;
import com.sprint.mission.discodeit.entity.User;


public interface AuthService {
    User login(LoginUserRequest request);
}
