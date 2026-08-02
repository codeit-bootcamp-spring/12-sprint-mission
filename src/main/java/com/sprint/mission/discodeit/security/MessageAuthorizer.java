package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// @PreAuthorize의 SpEL에서 참조하는 인가 검사용 컴포넌트
// 예) @PreAuthorize("@messageAuthorizer.isAuthor(#messageId, principal.userId)")
@RequiredArgsConstructor
@Component
public class MessageAuthorizer {

  private final MessageRepository messageRepository;

  @Transactional(readOnly = true)
  public boolean isAuthor(UUID messageId, UUID userId) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor() != null
            && message.getAuthor().getId().equals(userId))
        // 존재하지 않는 메시지는 403이 아니라 서비스에서 404로 처리되도록 통과시킨다
        .orElse(true);
  }
}
