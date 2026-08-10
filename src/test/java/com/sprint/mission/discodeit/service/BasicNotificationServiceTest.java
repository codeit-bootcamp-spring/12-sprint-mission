package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class BasicNotificationServiceTest {

  @Mock NotificationRepository notificationRepository;
  @Mock UserRepository userRepository;
  @Mock NotificationMapper notificationMapper;

  @InjectMocks BasicNotificationService notificationService;

  private UUID receiverId;
  private User receiver;
  private Notification notification;

  @BeforeEach
  void setUp() {
    receiverId = UUID.randomUUID();
    receiver = new User("receiver", "receiver@test.com", "encoded", null);
    org.springframework.test.util.ReflectionTestUtils.setField(receiver, "id", receiverId);
    notification = new Notification(receiver, "제목", "내용");
  }

  @Test
  @DisplayName("수신자 목록만큼 알림을 생성한다")
  void createAll_success() {
    UUID other = UUID.randomUUID();
    given(userRepository.getReferenceById(receiverId)).willReturn(receiver);
    given(userRepository.getReferenceById(other)).willReturn(receiver);

    notificationService.createAll(List.of(receiverId, other), "제목", "내용");

    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    then(notificationRepository).should().saveAll(captor.capture());
    assertThat(captor.getValue()).hasSize(2);
  }

  @Test
  @DisplayName("수신자가 없으면 저장하지 않는다")
  void createAll_emptyReceivers_skipsSave() {
    notificationService.createAll(List.of(), "제목", "내용");

    then(notificationRepository).should(never()).saveAll(org.mockito.ArgumentMatchers.anyList());
  }

  @Test
  @DisplayName("본인의 알림 목록을 조회한다")
  void findAllByReceiverId_success() {
    NotificationDto dto = new NotificationDto(UUID.randomUUID(), Instant.now(), receiverId,
        "제목", "내용");
    given(notificationRepository.findAllByReceiver_IdOrderByCreatedAtDesc(receiverId))
        .willReturn(List.of(notification));
    given(notificationMapper.toDto(notification)).willReturn(dto);

    List<NotificationDto> result = notificationService.findAllByReceiverId(receiverId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).receiverId()).isEqualTo(receiverId);
  }

  @Test
  @DisplayName("존재하지 않는 알림 확인 시 404")
  void delete_notFound_throwsException() {
    UUID notificationId = UUID.randomUUID();
    given(notificationRepository.findById(notificationId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.delete(notificationId, receiverId))
        .isInstanceOf(NotificationNotFoundException.class);
  }

  @Test
  @DisplayName("남의 알림을 확인하면 403")
  void delete_notOwner_throwsAccessDenied() {
    UUID notificationId = UUID.randomUUID();
    given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

    assertThatThrownBy(() -> notificationService.delete(notificationId, UUID.randomUUID()))
        .isInstanceOf(AccessDeniedException.class);
    then(notificationRepository).should(never()).delete(notification);
  }

  @Test
  @DisplayName("본인의 알림은 삭제된다")
  void delete_owner_success() {
    UUID notificationId = UUID.randomUUID();
    given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

    notificationService.delete(notificationId, receiverId);

    then(notificationRepository).should().delete(notification);
  }
}
