package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  // 이벤트 리스너가 대상자별로 알림을 생성한다
  void createAll(List<UUID> receiverIds, String title, String content);

  List<NotificationDto> findAllByReceiverId(UUID receiverId);

  // 알림 확인 = 삭제. 본인의 알림만 확인할 수 있다.
  void delete(UUID notificationId, UUID requesterId);
}
