package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
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
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Async("eventTaskExecutor")
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

  /**
   * 업로드 실패 통지.
   *
   * <p>이 이벤트는 트랜잭션 밖(비동기 업로드 스레드)에서 발행되므로
   * {@code @TransactionalEventListener}로 받으면 커밋될 트랜잭션이 없어 영영 실행되지 않는다.
   * 반드시 {@code @EventListener}여야 한다.
   */
  @Async("eventTaskExecutor")
  @EventListener
  public void onS3UploadFailedEvent(S3UploadFailedEvent event) {
    List<UUID> adminIds = userRepository.findAllByRole(Role.ADMIN).stream()
        .map(User::getId)
        .toList();
    if (adminIds.isEmpty()) {
      log.warn("업로드 실패를 통지할 관리자가 없습니다: binaryContentId={}", event.binaryContentId());
      return;
    }

    // 사후 디버깅에 필요한 정보를 한 화면에서 볼 수 있게 묶는다
    String content = String.format("RequestId: %s%nBinaryContentId: %s%nError: %s",
        event.requestId(), event.binaryContentId(), event.errorMessage());
    notificationService.createAll(adminIds, event.taskName() + " 실패", truncate(content));
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void onRoleUpdatedEvent(RoleUpdatedEvent event) {
    // 권한 변경은 당사자에게만 알린다
    notificationService.createAll(
        List.of(event.userId()),
        "권한이 변경되었습니다.",
        event.previousRole() + " -> " + event.newRole()
    );
  }

  // content 컬럼이 varchar(500)이라 스택 정보가 긴 실패 메시지는 잘라 담는다
  private String truncate(String content) {
    return content.length() <= 500 ? content : content.substring(0, 497) + "...";
  }

  // PRIVATE 채널은 이름이 없으므로 채널명 대신 DM으로 표기한다
  private String buildTitle(MessageCreatedEvent event) {
    String channelLabel = event.channelName() == null ? "DM" : "#" + event.channelName();
    return event.authorUsername() + " (" + channelLabel + ")";
  }
}
