package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChannelIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private ChannelService channelService;

  @Autowired
  private UserService userService;

  private UserResponse testUser1;
  private UserResponse testUser2;

  @BeforeEach
  void setUp() {
    testUser1 = userService.create(
        new UserCreateRequest("user1", "user1@codeit.com", "Password1234"), Optional.empty());
    testUser2 = userService.create(
        new UserCreateRequest("user2", "user2@codeit.com", "Password1234"), Optional.empty());
  }

  // CREATE

  @Test
  @DisplayName("[통합] 공개 채널 생성 성공")
  void createPublicChannel_Success() {
    // Given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "public-channel",
        "PUBLIC"
    );

    // When
    ChannelResponse response = channelService.create(request);

    // Then
    assertThat(response.id()).isNotNull();
    assertThat(response.type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(response.name()).isEqualTo("public-channel");
    assertThat(response.participants()).isEmpty();
  }

  @Test
  @DisplayName("[통합] 비공개 채널 생성 성공")
  void createPrivateChannel_Success() {
    // Given
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        "PRIVATE",
        List.of(testUser1.id(), testUser2.id())
    );

    // When
    ChannelResponse response = channelService.create(request);

    // Then
    assertThat(response.id()).isNotNull();
    assertThat(response.type()).isEqualTo(ChannelType.PRIVATE);
    assertThat(response.name()).isNull();
    assertThat(response.participants()).hasSize(2);
  }

  // UPDATE

  @Test
  @DisplayName("[통합] 공개 채널 수정 성공")
  void updatePublicChannel_Success() {
    // Given
    ChannelResponse created = channelService.create(
        new PublicChannelCreateRequest("original-channel", "PUBLIC"));

    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "updated-channel",
        "업데이트된 설명"
    );

    // When
    ChannelResponse updated = channelService.update(created.id(), updateRequest);

    // Then
    assertThat(updated.id()).isEqualTo(created.id());
    assertThat(updated.name()).isEqualTo("updated-channel");
  }

  @Test
  @DisplayName("[통합] 비공개 채널 수정 실패 - PrivateChannelUpdateException 발생")
  void updatePrivateChannel_Failure() {
    // Given
    ChannelResponse privateChannel = channelService.create(
        new PrivateChannelCreateRequest("PRIVATE", List.of(testUser1.id())));

    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "updated-channel",
        "설명"
    );

    // When & Then
    assertThatThrownBy(() -> channelService.update(privateChannel.id(), updateRequest))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  @Test
  @DisplayName("[통합] 채널 수정 실패 - 존재하지 않는 채널")
  void updateChannel_Failure_NotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "updated-channel",
        "설명"
    );

    // When & Then
    assertThatThrownBy(() -> channelService.update(nonExistentId, updateRequest))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // DELETE

  @Test
  @DisplayName("[통합] 채널 삭제 성공")
  void deleteChannel_Success() {
    // Given
    ChannelResponse created = channelService.create(
        new PublicChannelCreateRequest("delete-channel", "PUBLIC"));

    // When
    channelService.delete(created.id());

    // Then
    assertThatThrownBy(() -> channelService.delete(created.id()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("[통합] 채널 삭제 실패 - 존재하지 않는 채널")
  void deleteChannel_Failure_NotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> channelService.delete(nonExistentId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // FIND ALL

  @Test
  @DisplayName("[통합] 사용자별 채널 목록 조회 - 공개 채널 포함")
  void findAllByUserId_IncludesPublicChannels() {
    // Given
    channelService.create(new PublicChannelCreateRequest("public-channel", "PUBLIC"));

    // When
    List<ChannelResponse> channels = channelService.findAllByUserId(testUser1.id());

    // Then
    assertThat(channels).isNotEmpty();
    assertThat(channels).anyMatch(c -> c.type() == ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("[통합] 사용자별 채널 목록 조회 - 참여한 비공개 채널 포함")
  void findAllByUserId_IncludesPrivateChannels() {
    // Given
    channelService.create(
        new PrivateChannelCreateRequest("PRIVATE", List.of(testUser1.id())));

    // When
    List<ChannelResponse> channels = channelService.findAllByUserId(testUser1.id());

    // Then
    assertThat(channels).anyMatch(c -> c.type() == ChannelType.PRIVATE);
  }
}

