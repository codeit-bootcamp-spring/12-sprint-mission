package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 알림은 언제나 '내 알림'이므로 조회/삭제 대상자를 요청 파라미터가 아니라 인증 주체에서 얻는다
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @GetMapping
  @Override
  public ResponseEntity<List<NotificationDto>> findAll(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    List<NotificationDto> notifications =
        notificationService.findAllByReceiverId(userDetails.getUserId());
    return ResponseEntity.status(HttpStatus.OK).body(notifications);
  }

  @DeleteMapping(path = "{notificationId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("notificationId") UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    notificationService.delete(notificationId, userDetails.getUserId());
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
