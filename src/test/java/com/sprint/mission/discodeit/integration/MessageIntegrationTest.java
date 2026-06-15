package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private MessageService messageService;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  private UserResponse testUser;
  private ChannelResponse testChannel;

  @BeforeEach
  void setUp() {
    testUser = userService.create(
        new UserCreateRequest("testuser", "test@codeit.com", "Password1234"), Optional.empty());
    testChannel = channelService.create(
        new PublicChannelCreateRequest("test-channel", "PUBLIC"));
  }

  // CREATE

  @Test
  @DisplayName("[통합] 메시지 생성 성공")
  void createMessage_Success() {
    // Given
    MessageCreateRequest request = new MessageCreateRequest(
        testUser.id(),
        testChannel.id(),
        "테스트 메시지입니다."
    );

    // When
    MessageResponse response = messageService.create(request, List.of());

    // Then
    assertThat(response.id()).isNotNull();
    assertThat(response.channelId()).isEqualTo(testChannel.id());
    assertThat(response.author().id()).isEqualTo(testUser.id());
  }

  @Test
  @DisplayName("[통합] 메시지 생성 실패 - 존재하지 않는 채널")
  void createMessage_Failure_ChannelNotFound() {
    // Given
    UUID nonExistentChannelId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest(
        testUser.id(),
        nonExistentChannelId,
        "테스트 메시지입니다."
    );

    // When & Then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(RuntimeException.class);
  }

  // UPDATE

  @Test
  @DisplayName("[통합] 메시지 수정 성공")
  void updateMessage_Success() {
    // Given
    MessageCreateRequest createRequest = new MessageCreateRequest(
        testUser.id(),
        testChannel.id(),
        "테스트 메시지입니다."
    );
    MessageResponse created = messageService.create(createRequest, List.of());

    MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정된 내용입니다.");

    // When
    MessageResponse updated = messageService.update(created.id(), updateRequest);

    // Then
    assertThat(updated.id()).isEqualTo(created.id());
    assertThat(updated.content()).isEqualTo("수정된 내용입니다.");
  }

  @Test
  @DisplayName("[통합] 메시지 수정 실패 - 존재하지 않는 메시지")
  void updateMessage_Failure_MessageNotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정된 내용");

    // When & Then
    assertThatThrownBy(() -> messageService.update(nonExistentId, updateRequest))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // DELETE

  @Test
  @DisplayName("[통합] 메시지 삭제 성공")
  void deleteMessage_Success() {
    // Given
    MessageCreateRequest createRequest = new MessageCreateRequest(
        testUser.id(),
        testChannel.id(),
        "테스트 메시지입니다."
    );
    MessageResponse created = messageService.create(createRequest, List.of());

    // When
    messageService.delete(created.id());

    // Then
    assertThatThrownBy(() -> messageService.delete(created.id()))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("[통합] 메시지 삭제 실패 - 존재하지 않는 메시지")
  void deleteMessage_Failure_MessageNotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> messageService.delete(nonExistentId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // FIND

  @Test
  @DisplayName("[통합] 채널별 메시지 목록 조회 성공")
  void findAllByChannelId_Success() {
    // Given
    MessageCreateRequest request1 = new MessageCreateRequest(
        testUser.id(), testChannel.id(), "테스트 메시지입니다.");
    MessageCreateRequest request2 = new MessageCreateRequest(
        testUser.id(), testChannel.id(), "테스트 메시지입니다.");

    messageService.create(request1, List.of());
    messageService.create(request2, List.of());

    Pageable pageable = PageRequest.of(0, 50, Sort.Direction.DESC, "createdAt");

    // When
    PageResponse<MessageResponse> pageResponse = messageService.findAllByChannelId(
        testChannel.id(), null, pageable);

    // Then
    assertThat(pageResponse.content()).hasSizeGreaterThanOrEqualTo(2);
    assertThat(pageResponse.content())
        .allMatch(m -> m.channelId().equals(testChannel.id()));
  }

  @Test
  @DisplayName("[통합] 채널별 메시지 목록 조회 - 빈 채널")
  void findAllByChannelId_EmptyChannel() {
    // Given
    ChannelResponse emptyChannel = channelService.create(
        new PublicChannelCreateRequest("empty-channel", "PUBLIC"));

    Pageable pageable = PageRequest.of(0, 50, Sort.Direction.DESC, "createdAt");

    // When
    PageResponse<MessageResponse> pageResponse = messageService.findAllByChannelId(
        emptyChannel.id(), null, pageable);

    // Then
    assertThat(pageResponse.content()).isEmpty();
    assertThat(pageResponse.hasNext()).isFalse();
  }
}