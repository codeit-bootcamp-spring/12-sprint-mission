package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;

public interface AuthService {
    UserResponse updateRole(UserRoleUpdateRequest request);
    JwtInformation refresh(String refreshToken);

}
