package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.user.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

public interface AuthService {

  UserDto login(LoginRequest loginRequest);
}
