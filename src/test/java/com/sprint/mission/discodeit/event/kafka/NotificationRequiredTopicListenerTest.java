package com.sprint.mission.discodeit.event.kafka;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationDispatcher;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredTopicListenerTest {

  @Mock NotificationDispatcher notificationDispatcher;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private NotificationRequiredTopicListener listener() {
    return new NotificationRequiredTopicListener(notificationDispatcher, objectMapper);
  }

  @Test
  @DisplayName("정상 메시지는 알림 생성으로 이어진다")
  void onMessageCreatedEvent_dispatches() throws Exception {
    MessageCreatedEvent event = new MessageCreatedEvent(
        UUID.randomUUID(), UUID.randomUUID(), "announcements",
        UUID.randomUUID(), "system", "안녕");

    listener().onMessageCreatedEvent(objectMapper.writeValueAsString(event));

    then(notificationDispatcher).should().onMessageCreated(event);
  }

  @Test
  @DisplayName("깨진 메시지는 예외를 던지지 않고 건너뛴다 (poison pill 방지)")
  void onMessageCreatedEvent_brokenPayload_isSkipped() {
    // 예외를 던지면 offset이 진행되지 않아 같은 메시지를 무한히 다시 읽는다
    assertThatCode(() -> listener().onMessageCreatedEvent("{ 이건 JSON이 아님 }"))
        .doesNotThrowAnyException();

    then(notificationDispatcher).should(never())
        .onMessageCreated(org.mockito.ArgumentMatchers.any());
  }
}
