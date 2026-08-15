package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentStorageFailedEventListener {

  private final UserService userService;
  private final NotificationService notificationService;

  @EventListener
  public void handle(BinaryContentStorageFailedEvent event) {
    String content = """
        작업: %s
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """.formatted(
        event.taskName(),
        event.requestId(),
        event.binaryContentId(),
        event.errorMessage()
    );

    userService.findAll().stream()
        .filter(user -> user.role() == Role.ADMIN)
        .forEach(admin ->
            notificationService.create(
                admin.id(),
                "비동기 작업 최종 실패",
                content
            )
        );
  }
}