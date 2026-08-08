package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.auth.JwtGenerationException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtRegistry<UUID> jwtRegistry;
  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("역할 수정 실패(사용자 없음): userId={}", request.userId());
          return UserNotFoundException.withId(request.userId());
        });
    user.updateRole(request.newRole());

    jwtRegistry.invalidateJwtInformationByUserId(user.getId());
    return userMapper.toDto(user);
  }

  @Override
  public JwtInformation refreshToken(String refreshToken) {
    // 토큰 자체가 유효한지 검증하고, JWT 세션에서도 유효한지 검증하는 단계
    if (!tokenProvider.validateRefreshToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      // 인증 정보인 Refresh Token 원문은 로그에 남기지 않음
      log.info("유효하지 않거나 만료된 리프레시 토큰입니다.");
      throw InvalidRefreshTokenException.withToken();
    }

    String username = tokenProvider.getUsernameFromToken(refreshToken);
    UserDetails userDetails;
    try {
      userDetails = userDetailsService.loadUserByUsername(username);
    } catch (UsernameNotFoundException e) {
      throw InvalidRefreshTokenException.withToken();
    }

    try {
      DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) userDetails;
      String newAccessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
      String newRefreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);
      // 새로 발급한 토큰 원문은 로그에 남기지 않고 Registry에만 저장
      JwtInformation newJwtInformation = new JwtInformation(
          discodeitUserDetails.getUserDto()
          , newAccessToken
          , newRefreshToken
      );
      // JWT 세션의 기존 세션을 rotate 하는 메서드 호출 -> token 갱신용!
      jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);
      return newJwtInformation;
    } catch (JOSEException e) {
      log.error("토큰 재발급 실패: username={}", username, e);
      throw new JwtGenerationException(e);
    }
  }
}
