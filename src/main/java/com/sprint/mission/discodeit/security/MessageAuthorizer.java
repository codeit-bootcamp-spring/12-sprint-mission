package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// @PreAuthorize의 SpEL에서 참조하는 인가 검사용 컴포넌트
// 예) @PreAuthorize("@messageAuthorizer.isAuthor(#messageId, principal.userId)")
@RequiredArgsConstructor
@Component
public class MessageAuthorizer {

  private final MessageRepository messageRepository;

  // @Transactional을 붙이지 않아 호출한 서비스의 트랜잭션에 참여한다.
  // 별도 트랜잭션이면 영속성 컨텍스트가 달라 서비스에서 같은 메시지를 다시 조회할 때
  // 1차 캐시를 타지 못하고 쿼리가 두 번 나간다.
  public boolean isAuthor(UUID messageId, UUID userId) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor() != null
            && message.getAuthor().getId().equals(userId))
        // 존재하지 않는 메시지는 403이 아니라 서비스에서 404로 처리되도록 통과시킨다
        .orElse(true);
  }
}
