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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이벤트를 알림으로 바꾸는 실제 로직.
 *
 * <p>이벤트가 Spring Event로 오든 Kafka 토픽으로 오든 만들어지는 알림은 같아야 하므로,
 * 전달 수단(transport)과 알림 생성 규칙을 분리한다. 리스너 두 개는 각자의 방식으로 이벤트를
 * 받아 여기로 넘기기만 한다.
 *
 * <p>모든 메소드가 REQUIRES_NEW인 이유: Spring Event 경로에서는 커밋이 끝난 트랜잭션이 아직
 * 스레드에 묶여 있어 REQUIRED로 열면 그 트랜잭션에 조인해 변경이 flush되지 않는다.
 * Kafka 경로에서는 애초에 트랜잭션이 없어 어느 쪽이든 새로 열린다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationDispatcher {

  private static final int CONTENT_MAX_LENGTH = 500;

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  public void onMessageCreated(MessageCreatedEvent event) {
    List<ReadStatus> targets = readStatusRepository
        .findAllByChannel_IdAndNotificationEnabledTrue(event.channelId());

    // 자기가 보낸 메시지로 자기한테 알림이 가면 안 된다
    List<UUID> receiverIds = targets.stream()
        .map(readStatus -> readStatus.getUser().getId())
        .filter(userId -> !userId.equals(event.authorId()))
        .toList();

    notificationService.createAll(receiverIds, buildTitle(event), event.content());
  }

  public void onRoleUpdated(RoleUpdatedEvent event) {
    // 권한 변경은 당사자에게만 알린다
    notificationService.createAll(
        List.of(event.userId()),
        "권한이 변경되었습니다.",
        event.previousRole() + " -> " + event.newRole()
    );
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  public void onS3UploadFailed(S3UploadFailedEvent event) {
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

  // content 컬럼이 varchar(500)이라 스택 정보가 긴 실패 메시지는 잘라 담는다
  private String truncate(String content) {
    return content.length() <= CONTENT_MAX_LENGTH
        ? content
        : content.substring(0, CONTENT_MAX_LENGTH - 3) + "...";
  }

  // PRIVATE 채널은 이름이 없으므로 채널명 대신 DM으로 표기한다
  private String buildTitle(MessageCreatedEvent event) {
    String channelLabel = event.channelName() == null ? "DM" : "#" + event.channelName();
    return event.authorUsername() + " (" + channelLabel + ")";
  }
}
