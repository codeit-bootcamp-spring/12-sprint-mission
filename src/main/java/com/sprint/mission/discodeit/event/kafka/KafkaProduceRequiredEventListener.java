package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Spring Event를 Kafka 메시지로 변환해 서버 밖으로 내보내는 중계 구조.
 *
 * <p>발행 지점(서비스)은 그대로 두고 이 리스너만 갈아끼우면 되므로, 이벤트를 만드는 쪽은
 * 소비자가 같은 프로세스에 있는지 다른 서버에 있는지 알 필요가 없다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "discodeit.event.transport", havingValue = "kafka")
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    // 같은 채널의 메시지를 같은 파티션으로 보내야 알림 순서가 전송 순서와 어긋나지 않는다.
    // 순서는 토픽 전체가 아니라 파티션 안에서만 보장되기 때문이다.
    send(KafkaTopic.MESSAGE_CREATED, event.channelId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    // 같은 사용자의 권한 변경 이력은 순서대로 도착해야 한다
    send(KafkaTopic.ROLE_UPDATED, event.userId().toString(), event);
  }

  // 업로드 실패는 트랜잭션 밖에서 발행되므로 @EventListener여야 한다
  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    send(KafkaTopic.S3_UPLOAD_FAILED, event.binaryContentId().toString(), event);
  }

  private void send(String topic, String key, Object event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(topic, key, payload);
      log.debug("Kafka 발행: topic={}, key={}", topic, key);
    } catch (JsonProcessingException e) {
      // 직렬화 실패는 이벤트 정의가 잘못된 것이므로 재시도해도 달라지지 않는다
      log.error("Kafka 발행 실패 - 직렬화 불가: topic={}, key={}", topic, key, e);
    }
  }
}
