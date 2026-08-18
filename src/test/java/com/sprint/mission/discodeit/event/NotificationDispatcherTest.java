package com.sprint.mission.discodeit.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationDispatcherTest {

  @Mock ReadStatusRepository readStatusRepository;
  @Mock UserRepository userRepository;
  @Mock NotificationService notificationService;

  @InjectMocks NotificationDispatcher dispatcher;

  private User userWithId(String username) {
    User user = new User(username, username + "@test.com", "encoded", null);
    ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
    return user;
  }

  @Test
  @DisplayName("알림을 켠 구독자에게 알림을 생성하고 작성자는 제외한다")
  void onMessageCreatedEvent_excludesAuthor() {
    Channel channel = new Channel(ChannelType.PUBLIC, "announcements", null);
    UUID channelId = UUID.randomUUID();
    ReflectionTestUtils.setField(channel, "id", channelId);

    User author = userWithId("system");
    User subscriber = userWithId("someone");
    given(readStatusRepository.findAllByChannel_IdAndNotificationEnabledTrue(channelId))
        .willReturn(List.of(
            new ReadStatus(author, channel, Instant.now()),
            new ReadStatus(subscriber, channel, Instant.now())));

    dispatcher.onMessageCreated(new MessageCreatedEvent(
        UUID.randomUUID(), channelId, "announcements",
        author.getId(), "system", "이번 주 일정을 공유드립니다."));

    ArgumentCaptor<List<UUID>> receivers = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
    then(notificationService).should()
        .createAll(receivers.capture(), title.capture(), org.mockito.ArgumentMatchers.eq(
            "이번 주 일정을 공유드립니다."));

    assertThat(receivers.getValue()).containsExactly(subscriber.getId());
    assertThat(title.getValue()).isEqualTo("system (#announcements)");
  }

  @Test
  @DisplayName("이름이 없는 PRIVATE 채널은 DM으로 표기한다")
  void onMessageCreatedEvent_privateChannelTitle() {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    UUID channelId = UUID.randomUUID();
    ReflectionTestUtils.setField(channel, "id", channelId);

    User author = userWithId("author");
    User subscriber = userWithId("subscriber");
    given(readStatusRepository.findAllByChannel_IdAndNotificationEnabledTrue(channelId))
        .willReturn(List.of(new ReadStatus(subscriber, channel, Instant.now())));

    dispatcher.onMessageCreated(new MessageCreatedEvent(
        UUID.randomUUID(), channelId, null, author.getId(), "author", "안녕"));

    ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
    then(notificationService).should().createAll(
        org.mockito.ArgumentMatchers.anyList(), title.capture(),
        org.mockito.ArgumentMatchers.eq("안녕"));
    assertThat(title.getValue()).isEqualTo("author (DM)");
  }

  @Test
  @DisplayName("업로드 실패는 ADMIN 전원에게 디버깅 정보와 함께 통지한다")
  void onS3UploadFailedEvent_notifiesAdmins() {
    User admin = userWithId("admin");
    given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of(admin));
    UUID binaryContentId = UUID.randomUUID();

    dispatcher.onS3UploadFailed(new S3UploadFailedEvent(
        "S3 파일 업로드", "7641467e", binaryContentId, "The AWS Access Key Id ... (Status Code: 403)"));

    ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
    then(notificationService).should().createAll(
        org.mockito.ArgumentMatchers.eq(List.of(admin.getId())),
        org.mockito.ArgumentMatchers.eq("S3 파일 업로드 실패"),
        content.capture());

    assertThat(content.getValue())
        .contains("RequestId: 7641467e")
        .contains("BinaryContentId: " + binaryContentId)
        .contains("Status Code: 403");
  }

  @Test
  @DisplayName("관리자가 없으면 통지를 건너뛴다")
  void onS3UploadFailedEvent_noAdmin_skips() {
    given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of());

    dispatcher.onS3UploadFailed(new S3UploadFailedEvent(
        "S3 파일 업로드", "req", UUID.randomUUID(), "boom"));

    then(notificationService).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("권한 변경은 당사자에게만 알린다")
  void onRoleUpdatedEvent_notifiesOwnerOnly() {
    UUID userId = UUID.randomUUID();

    dispatcher.onRoleUpdated(new RoleUpdatedEvent(userId, Role.USER, Role.CHANNEL_MANAGER));

    then(notificationService).should().createAll(
        List.of(userId), "권한이 변경되었습니다.", "USER -> CHANNEL_MANAGER");
  }
}
