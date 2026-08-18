package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.mission.discodeit.service.NotificationService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final NotificationMapper notificationMapper;
  private final CacheManager cacheManager;

  // BinaryContent.updateStatus와 같은 이유로 새 트랜잭션이 필요하다.
  // 이 메소드는 원본 트랜잭션이 커밋된 뒤 이벤트 리스너에서 호출된다.
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createAll(List<UUID> receiverIds, String title, String content) {
    if (receiverIds.isEmpty()) {
      return;
    }
    // getReferenceById로 프록시만 잡아 두면 수신자 수만큼 SELECT가 나가지 않는다
    List<Notification> notifications = receiverIds.stream()
        .map(receiverId -> {
          User receiver = userRepository.getReferenceById(receiverId);
          return new Notification(receiver, title, content);
        })
        .toList();
    notificationRepository.saveAll(notifications);
    // 수신자를 알고 있으므로 캐시 전체를 비우지 않고 해당 키만 지운다.
    // 수신자가 여러 명이라 어노테이션(@CacheEvict) 하나로는 표현할 수 없어 CacheManager를 직접 쓴다.
    evictNotificationCache(receiverIds);
    log.info("알림 생성: 수신자 {}명, title={}", notifications.size(), title);
  }

  private void evictNotificationCache(List<UUID> receiverIds) {
    Cache cache = cacheManager.getCache(CacheConfig.NOTIFICATIONS);
    if (cache == null) {
      return;
    }
    receiverIds.forEach(cache::evict);
  }

  @Override
  @Cacheable(cacheNames = CacheConfig.NOTIFICATIONS, key = "#receiverId")
  @Transactional(readOnly = true)
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiver_IdOrderByCreatedAtDesc(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  // 확인한 사람의 알림 목록만 무효화하면 된다
  @Override
  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS, key = "#requesterId")
  public void delete(UUID notificationId, UUID requesterId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    // 존재 여부를 먼저 확인해야 없는 알림에 404, 남의 알림에 403이 나간다
    if (!notification.getReceiver().getId().equals(requesterId)) {
      throw new AccessDeniedException("본인의 알림만 확인할 수 있습니다.");
    }
    notificationRepository.delete(notification);
    log.info("알림 확인: id={}, receiverId={}", notificationId, requesterId);
  }
}
