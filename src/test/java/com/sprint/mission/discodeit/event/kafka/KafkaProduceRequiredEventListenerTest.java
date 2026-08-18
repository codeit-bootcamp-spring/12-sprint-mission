package com.sprint.mission.discodeit.event.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class KafkaProduceRequiredEventListenerTest {

  @Mock KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private KafkaProduceRequiredEventListener listener() {
    return new KafkaProduceRequiredEventListener(kafkaTemplate, objectMapper);
  }

  @Test
  @DisplayName("메시지 이벤트는 channelId를 key로 발행해 채널 단위 순서를 보장한다")
  void on_messageCreated_usesChannelIdAsKey() {
    UUID channelId = UUID.randomUUID();
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(), channelId, "announcements",
        UUID.randomUUID(), "system", "이번 주 일정을 공유드립니다.");

    listener().on(event);

    ArgumentCaptor<String> topic = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
    then(kafkaTemplate).should().send(topic.capture(), key.capture(), payload.capture());

    assertThat(topic.getValue()).isEqualTo("discodeit.MessageCreatedEvent");
    assertThat(key.getValue()).isEqualTo(channelId.toString());
    assertThat(payload.getValue()).contains("announcements").contains("system");
  }

  @Test
  @DisplayName("발행한 payload는 소비 측에서 원래 이벤트로 복원된다")
  void payload_roundTrips() throws Exception {
    RoleUpdatedEvent event =
        new RoleUpdatedEvent(UUID.randomUUID(), Role.USER, Role.CHANNEL_MANAGER);

    listener().on(event);

    ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
    then(kafkaTemplate).should()
        .send(org.mockito.ArgumentMatchers.eq("discodeit.RoleUpdatedEvent"),
            org.mockito.ArgumentMatchers.eq(event.userId().toString()), payload.capture());

    RoleUpdatedEvent restored =
        objectMapper.readValue(payload.getValue(), RoleUpdatedEvent.class);
    assertThat(restored).isEqualTo(event);
  }
}
