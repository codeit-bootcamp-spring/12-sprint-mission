package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 사용자의 접속 여부를 판단한다.
 *
 * <p>접속 여부는 엔티티에 저장된 값이 아니라 조회 시점의 런타임 상태다. 이 판단을 무엇으로 하는지는
 * 인증 방식에 따라 바뀌므로(세션 레지스트리 → 토큰 레지스트리), 그 의존을 이 컴포넌트 한 곳에 가둔다.
 * 덕분에 UserMapper는 보안 인프라를 직접 알 필요가 없다.
 */
@RequiredArgsConstructor
@Component
public class UserOnlineChecker {

  private final JwtRegistry jwtRegistry;

  public boolean isOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}
