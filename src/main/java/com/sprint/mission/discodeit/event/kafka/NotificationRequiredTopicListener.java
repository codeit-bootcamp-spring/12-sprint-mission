package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationDispatcher;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 메인 서비스와 분리된 별도 서버라고 가정한 알림 서비스.
 *
 * <p>메인 서비스의 메모리가 아니라 Kafka 토픽에서 이벤트를 읽으므로, 실제로 프로세스를 분리해도
 * 이 클래스는 그대로 동작한다.
 *
 * <p>Kafka는 at-least-once 전달이라 같은 메시지를 두 번 받을 수 있다. 즉 알림이 중복 생성될
 * 여지가 있으며, 이를 막으려면 이벤트에 고유 ID를 싣고 소비 측에서 멱등하게 처리해야 한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "discodeit.event.transport", havingValue = "kafka")
public class NotificationRequiredTopicListener {

  private final NotificationDispatcher notificationDispatcher;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = KafkaTopic.MESSAGE_CREATED)
  public void onMessageCreatedEvent(String kafkaEvent) {
    read(kafkaEvent, MessageCreatedEvent.class)
        .ifPresent(notificationDispatcher::onMessageCreated);
  }

  @KafkaListener(topics = KafkaTopic.ROLE_UPDATED)
  public void onRoleUpdatedEvent(String kafkaEvent) {
    read(kafkaEvent, RoleUpdatedEvent.class)
        .ifPresent(notificationDispatcher::onRoleUpdated);
  }

  @KafkaListener(topics = KafkaTopic.S3_UPLOAD_FAILED)
  public void onS3UploadFailedEvent(String kafkaEvent) {
    read(kafkaEvent, S3UploadFailedEvent.class)
        .ifPresent(notificationDispatcher::onS3UploadFailed);
  }

  /**
   * 역직렬화에 실패한 메시지는 건너뛴다.
   *
   * <p>여기서 예외를 던지면 offset이 진행되지 않아 같은 메시지를 무한히 다시 읽는다. 메시지 내용은
   * 재시도해도 달라지지 않으므로, 깨진 메시지 하나가 그 파티션 뒤의 모든 알림을 막아버린다
   * (poison pill). 운영에서는 dead letter 토픽으로 보내 따로 처리한다.
   */
  private <T> Optional<T> read(String kafkaEvent, Class<T> type) {
    try {
      return Optional.of(objectMapper.readValue(kafkaEvent, type));
    } catch (JsonProcessingException e) {
      log.error("Kafka 메시지 역직렬화 실패 - 건너뜀: type={}, payload={}",
          type.getSimpleName(), kafkaEvent, e);
      return Optional.empty();
    }
  }
}
