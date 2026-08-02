package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
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
  private final SessionRegistry sessionRegistry;

  // 세션에 저장된 Principal은 로그인 시점의 스냅샷이라 최신 정보가 아닐 수 있으므로 DB에서 다시 조회한다
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

    // 변경된 권한이 즉시 반영되도록, 로그인 중이라면 세션을 만료시켜 재로그인을 유도
    expireSessions(request.userId());

    return userMapper.toDto(user);
  }

  private void expireSessions(UUID userId) {
    sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(userDetails -> userDetails.getUserId().equals(userId))
        .flatMap(userDetails -> sessionRegistry.getAllSessions(userDetails, false).stream())
        .forEach(SessionInformation::expireNow);
  }
}
