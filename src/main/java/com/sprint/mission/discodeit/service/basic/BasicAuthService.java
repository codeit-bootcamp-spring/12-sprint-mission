package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.RotationResult;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final ApplicationEventPublisher eventPublisher;


  // 사용자 권한 수정은 관리자만 가능
  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));
    Role previousRole = user.getRole();
    user.updateRole(request.newRole());
    log.info("사용자 권한 변경: userId={}, newRole={}", request.userId(), request.newRole());

    // 권한이 실제로 바뀐 경우에만 알린다
    if (previousRole != request.newRole()) {
      eventPublisher.publishEvent(
          new RoleUpdatedEvent(request.userId(), previousRole, request.newRole()));
    }

    // 토큰에는 발급 시점의 권한이 박혀 있어 스스로 갱신되지 않는다.
    // 변경된 권한이 즉시 반영되도록 발급된 토큰을 모두 무효화해 재로그인을 유도한다.
    //
    // 커밋 이후로 미루는 이유: 레지스트리는 트랜잭션에 참여하지 않는 인메모리 자원이라 롤백해도
    // 되돌아가지 않는다. 여기서 바로 지우면 이후 트랜잭션이 실패했을 때 "권한은 그대로인데
    // 사용자만 강제 로그아웃"된 상태가 남는다.
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        jwtRegistry.invalidateJwtInformationByUserId(request.userId());
      }
    });

    return userMapper.toDto(user);
  }

  @Override
  public JwtInformation refresh(String refreshToken) {
    // 1) 토큰 자체의 유효성 (서명, 만료, 용도)
    if (!jwtTokenProvider.isValidRefreshToken(refreshToken)) {
      throw new InvalidRefreshTokenException("서명이 유효하지 않거나 만료된 토큰");
    }

    UUID userId = jwtTokenProvider.getUserId(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    UserDto userDto = userMapper.toDto(user);

    // 2) 활성 여부 확인과 교체를 한 번의 원자적 연산으로 수행한다.
    //    따로 검사한 뒤 교체하면 그 사이에 다른 요청이 끼어들어, 탭 두 개를 열어둔 정상 사용자가
    //    재사용으로 오인되거나 자기가 만들지 않은 토큰을 응답으로 받을 수 있다.
    JwtInformation generated = jwtTokenProvider.generate(userDto);
    RotationResult result = jwtRegistry.rotateJwtInformation(
        refreshToken, generated.getAccessToken(), generated.getRefreshToken());

    // 3) 어느 쪽과도 맞지 않으면 이미 로테이션되었거나 무효화된 토큰이다.
    //    정상 사용자는 이런 토큰을 들고 올 수 없으므로 탈취 후 재사용으로 간주하고,
    //    공격자가 앞서 발급받아 갔을 토큰까지 함께 죽이기 위해 해당 사용자의 토큰을 모두 무효화한다.
    if (!result.isAccepted()) {
      log.warn("리프레시 토큰 재사용 감지, 해당 사용자의 모든 토큰을 무효화합니다: userId={}", userId);
      jwtRegistry.invalidateJwtInformationByUserId(userId);
      throw new InvalidRefreshTokenException("이미 사용된 리프레시 토큰");
    }

    if (result.outcome() == RotationResult.Outcome.GRACE_REPLAY) {
      // 유예 창 안의 중복 요청. 새로 만든 토큰은 등록되지 않았으므로 버리고 현재 쌍을 그대로 준다.
      log.debug("유예 창 안의 중복 재발급 요청, 현재 토큰 쌍을 재응답합니다: userId={}", userId);
    }

    // 로테이션이 끝나야 새 엑세스 토큰이 활성으로 잡히므로, online이 반영된 최신 정보를 다시 매핑한다
    return new JwtInformation(userMapper.toDto(user),
        result.tokens().accessToken(), result.tokens().refreshToken());
  }
}
