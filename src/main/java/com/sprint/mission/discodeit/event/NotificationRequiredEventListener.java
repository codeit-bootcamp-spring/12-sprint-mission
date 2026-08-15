package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final MessageService messageService;
  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;

  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    MessageDto message = messageService.find(event.messageId());

    Channel channel = channelRepository.findById(message.channelId())
        .orElseThrow();

    List<ReadStatus> readStatuses =
        readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
            message.channelId()
        );

    readStatuses.stream()
        .filter(readStatus ->
            !readStatus.getUser().getId().equals(message.author().id())
        )
        .forEach(readStatus ->
            notificationService.create(
                readStatus.getUser().getId(),
                message.author().username() + " (#" + channel.getName() + ")",
                message.content()
            )
        );
  }


  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    notificationService.create(
        event.userId(),
        "권한이 변경되었습니다.",
        event.oldRole() + " -> " + event.newRole()
    );
  }
}