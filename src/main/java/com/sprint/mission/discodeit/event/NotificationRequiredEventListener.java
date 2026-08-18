package com.sprint.mission.discodeit.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 같은 프로세스 안에서 이벤트를 받아 알림을 생성한다.
 *
 * <p>Kafka를 쓰는 구성에서는 알림 생성을 별도 서비스가 맡으므로 이 리스너는 등록되지 않는다.
 * 코드를 지우는 대신 설정으로 끄는 이유는, 브로커 없이도 애플리케이션이 온전히 동작해야 하고
 * 두 방식의 동작을 나란히 비교할 수 있어야 하기 때문이다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "discodeit.event.transport", havingValue = "spring",
    matchIfMissing = true)
public class NotificationRequiredEventListener {

  private final NotificationDispatcher notificationDispatcher;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void onMessageCreatedEvent(MessageCreatedEvent event) {
    notificationDispatcher.onMessageCreated(event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void onRoleUpdatedEvent(RoleUpdatedEvent event) {
    notificationDispatcher.onRoleUpdated(event);
  }

  /**
   * 업로드 실패는 트랜잭션 밖(비동기 업로드 스레드)에서 발행되므로 커밋될 트랜잭션이 없다.
   * {@code @TransactionalEventListener}로 받으면 영영 실행되지 않으므로 {@code @EventListener}여야 한다.
   */
  @Async("eventTaskExecutor")
  @EventListener
  public void onS3UploadFailedEvent(S3UploadFailedEvent event) {
    notificationDispatcher.onS3UploadFailed(event);
  }
}
