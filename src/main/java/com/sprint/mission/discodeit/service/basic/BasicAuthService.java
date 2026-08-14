package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.exception.user.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserService userService;

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(RoleUpdateRequest request) {
    return updateRoleInternal(request);
  }

  @Transactional
  @Override
  public UserDto updateRoleInternal(RoleUpdateRequest request) {
    UserDto updatedUser = userService.updateRole(request);

    jwtRegistry.invalidateJwtInformationByUserId(request.userId());

    return updatedUser;
  }

  @Override
  public JwtInformation refreshToken(String refreshToken) {
    if (!jwtTokenProvider.validateRefreshToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new InvalidRefreshTokenException();
    }
    String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

    try {
      String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

      JwtInformation jwtInformation =
          new JwtInformation(
              userDetails.getUserDto(),
              newAccessToken,
              newRefreshToken
          );

      jwtRegistry.rotateJwtInformation(refreshToken, jwtInformation);

      return jwtInformation;
    } catch (Exception e) {
      throw new RuntimeException("Failed to refresh token", e);
    }
  }
}
