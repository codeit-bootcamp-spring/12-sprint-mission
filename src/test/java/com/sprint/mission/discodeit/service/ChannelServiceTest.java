package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @InjectMocks
  private BasicChannelService channelService;

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;

  // create PUBLIC
  @Test
  @DisplayName("create PUBLIC 성공")
  void createPublic_success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "PUBLIC");
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "공지", null);
    ChannelResponse mockResponse = new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC, "공지",
        null, null, null);

    given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
    given(channelMapper.toDto(any(Channel.class))).willReturn(mockResponse);

    // when
    ChannelResponse result = channelService.create(request);

    // then
    assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(result.name()).isEqualTo("공지");
    then(channelRepository).should().save(any(Channel.class));
  }

  @Test
  @DisplayName("create PUBLIC 실패 - 저장 중 예외 발생")
  void createPublic_fail_repositoryError() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "PUBLIC");
    given(channelRepository.save(any(Channel.class)))
        .willThrow(new RuntimeException("DB 저장 실패"));

    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(RuntimeException.class);
  }

  // create PRIVATE
  @Test
  @DisplayName("create PRIVATE 성공")
  void createPrivate_success() {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    List<UUID> participantIds = List.of(userId1, userId2);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest("PRIVATE",
        participantIds);

    Channel mockChannel = new Channel(ChannelType.PRIVATE, null, null);
    List<User> mockUsers = List.of(
        new User("user1", "user1@example.com", "pw", null, Role.ADMIN),
        new User("user2", "user2@example.com", "pw", null, Role.ADMIN)
    );
    ChannelResponse mockResponse = new ChannelResponse(UUID.randomUUID(), ChannelType.PRIVATE, null,
        null, null, null);

    given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
    given(userRepository.findAllById(participantIds)).willReturn(mockUsers);
    given(readStatusRepository.saveAll(anyList())).willReturn(List.of());
    given(channelMapper.toDto(any(Channel.class))).willReturn(mockResponse);

    // when
    ChannelResponse result = channelService.create(request);

    // then
    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
    then(userRepository).should().findAllById(participantIds);
    then(readStatusRepository).should().saveAll(anyList());
  }

  @Test
  @DisplayName("create PRIVATE 실패 - 저장 중 예외 발생")
  void createPrivate_fail_repositoryError() {
    List<UUID> participantIds = List.of(UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest("PRIVATE",
        participantIds);
    given(channelRepository.save(any(Channel.class)))
        .willThrow(new RuntimeException("DB 저장 실패"));

    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(RuntimeException.class);
  }

  // update
  @Test
  @DisplayName("update 성공")
  void update_success() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널명", "새설명");
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "공지", null);
    ChannelResponse mockResponse = new ChannelResponse(channelId, ChannelType.PUBLIC, "새채널명", "새설명",
        null, null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(mockResponse);

    // when
    ChannelResponse result = channelService.update(channelId, request);

    // then
    assertThat(result.name()).isEqualTo("새채널명");
  }

  @Test
  @DisplayName("update 실패 - 존재하지 않는 채널")
  void update_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널명", "새설명");
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  @DisplayName("update 실패 - PRIVATE 채널 수정 불가")
  void update_fail_privateChannel() {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새채널명", "새설명");
    Channel mockChannel = new Channel(ChannelType.PRIVATE, null, null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // delete
  @Test
  @DisplayName("delete 성공")
  void delete_success() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(true);

    // when
    channelService.delete(channelId);

    // then
    then(messageRepository).should().deleteAllByChannelId(channelId);
    then(readStatusRepository).should().deleteAllByChannelId(channelId);
    then(channelRepository).should().deleteById(channelId);
  }

  @Test
  @DisplayName("delete 실패 - 존재하지 않는 채널")
  void delete_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(NoSuchElementException.class);
    then(messageRepository).shouldHaveNoInteractions();
    then(readStatusRepository).shouldHaveNoInteractions();
    then(channelRepository).should().existsById(channelId);
    then(channelRepository).shouldHaveNoMoreInteractions();
  }

  // findAllByUserId
  @Test
  @DisplayName("findAllByUserId 성공 - PUBLIC + 구독 채널 반환")
  void findAllByUserId_success() {
    // given
    UUID userId = UUID.randomUUID();
    Channel publicChannel = new Channel(ChannelType.PUBLIC, "공지", null);
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    ReadStatus readStatus = new ReadStatus(null, privateChannel, null);
    ChannelResponse mockResponse = new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC,
        "공지", null, null, null);

    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
    given(channelRepository.findAllByTypeOrIdIn(any(ChannelType.class), anyList()))
        .willReturn(List.of(publicChannel, privateChannel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(mockResponse);

    // when
    List<ChannelResponse> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(2);
    then(readStatusRepository).should().findAllByUserId(userId);
    then(channelRepository).should().findAllByTypeOrIdIn(any(), anyList());
  }

  @Test
  @DisplayName("findAllByUserId - 구독 채널 없으면 PUBLIC만 반환")
  void findAllByUserId_noSubscribed() {
    // given
    UUID userId = UUID.randomUUID();
    Channel publicChannel = new Channel(ChannelType.PUBLIC, "공지", null);
    ChannelResponse mockResponse = new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC, "공지",
        null, null, null);

    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of()))
        .willReturn(List.of(publicChannel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(mockResponse);

    // when
    List<ChannelResponse> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(1);
  }
}