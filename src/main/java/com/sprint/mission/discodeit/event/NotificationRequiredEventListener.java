package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

// 알림이 필요한 이벤트를 받아 실제 알림을 생성한다.
// 알림 생성이 실패해도 메시지 전송/권한 변경 자체는 이미 커밋되어 되돌아가지 않는다.
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;

  @TransactionalEventListener
  // 커밋 이후라 ReadStatus 조회에 트랜잭션이 필요한데, 이미 끝난 트랜잭션에 조인하면 안 된다.
  // Spring도 @TransactionalEventListener에는 REQUIRES_NEW / NOT_SUPPORTED만 허용한다.
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  public void onMessageCreatedEvent(MessageCreatedEvent event) {
    List<ReadStatus> targets = readStatusRepository
        .findAllByChannel_IdAndNotificationEnabledTrue(event.channelId());

    // 자기가 보낸 메시지로 자기한테 알림이 가면 안 된다
    List<UUID> receiverIds = targets.stream()
        .map(readStatus -> readStatus.getUser().getId())
        .filter(userId -> !userId.equals(event.authorId()))
        .toList();

    notificationService.createAll(receiverIds, buildTitle(event), event.content());
  }

  @TransactionalEventListener
  public void onRoleUpdatedEvent(RoleUpdatedEvent event) {
    // 권한 변경은 당사자에게만 알린다
    notificationService.createAll(
        List.of(event.userId()),
        "권한이 변경되었습니다.",
        event.previousRole() + " -> " + event.newRole()
    );
  }

  // PRIVATE 채널은 이름이 없으므로 채널명 대신 DM으로 표기한다
  private String buildTitle(MessageCreatedEvent event) {
    String channelLabel = event.channelName() == null ? "DM" : "#" + event.channelName();
    return event.authorUsername() + " (" + channelLabel + ")";
  }
}
