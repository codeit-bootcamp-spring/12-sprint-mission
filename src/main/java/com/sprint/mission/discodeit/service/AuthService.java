package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import java.util.UUID;

public interface AuthService {

  UserDto me(UUID userId);

  UserDto updateRole(UserRoleUpdateRequest request);
}
