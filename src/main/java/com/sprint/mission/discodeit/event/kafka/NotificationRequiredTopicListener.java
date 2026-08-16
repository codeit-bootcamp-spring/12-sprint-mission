package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.BinaryContentStorageFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final MessageService messageService;
  private final ChannelService channelService;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final UserService userService;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event =
          objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      MessageDto message = messageService.find(event.messageId());
      ChannelDto channel = channelService.find(message.channelId());

      readStatusRepository.findAllByChannelIdWithUser(message.channelId())
          .stream()
          .filter(readStatus ->
              !readStatus.getUser().getId().equals(message.author().id()))
          .forEach(readStatus ->
              notificationService.create(
                  readStatus.getUser().getId(),
                  message.author().username() + " (#" + channel.name() + ")",
                  message.content()
              )
          );

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event =
          objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      notificationService.create(
          event.userId(),
          "권한이 변경되었습니다.",
          event.oldRole() + " -> " + event.newRole()
      );

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      BinaryContentStorageFailedEvent event =
          objectMapper.readValue(
              kafkaEvent,
              BinaryContentStorageFailedEvent.class
          );

      String content =
          "작업: " + event.taskName()
              + "\nRequestId: " + event.requestId()
              + "\nBinaryContentId: " + event.binaryContentId()
              + "\nError: " + event.errorMessage();

      userService.findAll().stream()
          .filter(user -> user.role() == Role.ADMIN)
          .forEach(admin ->
              notificationService.create(
                  admin.id(),
                  "비동기 작업 최종 실패",
                  content
              )
          );

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}