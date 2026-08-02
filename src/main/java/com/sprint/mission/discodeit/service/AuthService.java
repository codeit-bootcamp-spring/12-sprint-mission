package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.UUID;

public interface AuthService {

  UserDto me(UUID userId);
}
