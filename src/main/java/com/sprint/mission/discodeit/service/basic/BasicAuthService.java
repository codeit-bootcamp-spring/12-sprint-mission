package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.exception.user.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

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
    UUID userId = request.userId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Role newRole = request.newRole();
    user.updateRole(newRole);

    return userMapper.toDto(user);
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
