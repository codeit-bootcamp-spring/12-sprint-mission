package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
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
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  // 토큰에 담긴 사용자 정보는 발급 시점의 스냅샷이라 최신 정보가 아닐 수 있으므로 DB에서 다시 조회한다
  @Override
  public UserDto me(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  // 사용자 권한 수정은 관리자만 가능
  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));
    user.updateRole(request.newRole());
    log.info("사용자 권한 변경: userId={}, newRole={}", request.userId(), request.newRole());

    // 토큰에는 발급 시점의 권한이 박혀 있어 스스로 갱신되지 않는다.
    // 변경된 권한이 즉시 반영되도록 발급된 토큰을 모두 무효화해 재로그인을 유도한다.
    jwtRegistry.invalidateJwtInformationByUserId(request.userId());

    return userMapper.toDto(user);
  }

  @Override
  public JwtInformation refresh(String refreshToken) {
    // 1) 토큰 자체의 유효성 (서명, 만료, 용도)
    if (!jwtTokenProvider.isValidRefreshToken(refreshToken)) {
      throw new InvalidRefreshTokenException("서명이 유효하지 않거나 만료된 토큰");
    }

    UUID userId = jwtTokenProvider.getUserId(refreshToken);

    // 2) 서명은 멀쩡한데 레지스트리에 없다면 이미 로테이션되었거나 무효화된 토큰이다.
    //    정상 사용자는 이런 토큰을 들고 올 수 없으므로 탈취 후 재사용으로 간주하고,
    //    공격자가 앞서 발급받아 갔을 토큰까지 함께 죽이기 위해 해당 사용자의 토큰을 모두 무효화한다.
    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      log.warn("리프레시 토큰 재사용 감지, 해당 사용자의 모든 토큰을 무효화합니다: userId={}", userId);
      jwtRegistry.invalidateJwtInformationByUserId(userId);
      throw new InvalidRefreshTokenException("이미 사용된 리프레시 토큰");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    JwtInformation rotated = jwtRegistry
        .rotateJwtInformation(refreshToken, jwtTokenProvider.generate(userMapper.toDto(user)))
        .orElseThrow(() -> new InvalidRefreshTokenException("로테이션 대상 토큰을 찾을 수 없음"));

    // 로테이션이 끝나야 새 엑세스 토큰이 활성으로 잡히므로, online이 반영된 최신 정보를 다시 매핑한다
    return new JwtInformation(userMapper.toDto(user), rotated.getAccessToken(),
        rotated.getRefreshToken());
  }
}
