package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 권한이 바뀐 사용자의 토큰을 무효화해 재로그인을 유도한다.
 *
 * <p>토큰에는 발급 시점의 권한이 박혀 있어 스스로 갱신되지 않기 때문이다.
 *
 * <p>커밋 이후에 실행하는 이유: 레지스트리는 트랜잭션에 참여하지 않는 인메모리 자원이라 롤백해도
 * 되돌아가지 않는다. 권한 변경 트랜잭션이 뒤늦게 실패하면 "권한은 그대로인데 사용자만 강제
 * 로그아웃"된 상태가 남는다.
 *
 * <p>이벤트로 받으면 서비스가 보안 인프라를 직접 의존하지 않아도 된다. 다만 이 무효화는 이 서버의
 * 메모리를 정리하는 국소적인 작업이므로, 이벤트를 Kafka로 내보내는 구성에서도 항상 등록된다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RoleUpdatedTokenInvalidator {

  private final JwtRegistry jwtRegistry;

  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    log.info("권한 변경으로 토큰 무효화: userId={}, {} -> {}",
        event.userId(), event.previousRole(), event.newRole());
    jwtRegistry.invalidateJwtInformationByUserId(event.userId());
  }
}
