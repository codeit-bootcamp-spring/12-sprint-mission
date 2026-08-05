package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.TokenRefreshResult;

public interface AuthService {

  TokenRefreshResult refresh(String refreshToken);
}
