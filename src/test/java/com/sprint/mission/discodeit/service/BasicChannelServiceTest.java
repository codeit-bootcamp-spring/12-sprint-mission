package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock ChannelRepository channelRepository;
  @Mock ReadStatusRepository readStatusRepository;
  @Mock MessageRepository messageRepository;
  @Mock UserRepository userRepository;
  @Mock ChannelMapper channelMapper;
  @Mock UserMapper userMapper;

  @InjectMocks BasicChannelService channelService;

  private UUID channelId;
  private Channel publicChannel;
  private Channel privateChannel;
  private ChannelDto channelDto;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();
    publicChannel = new Channel(ChannelType.PUBLIC, "general", "General channel");
    privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "general", "General channel",
        List.of(), Instant.now());
  }

  // ── create PUBLIC ────────────────────────────────────────────

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void createPublic_success() {
    // given
    PublicChannelCreateRequest req = new PublicChannelCreateRequest("general", "General channel");
    given(channelRepository.save(any(Channel.class))).willReturn(publicChannel);
    given(channelMapper.toDto(any(Channel.class), any(), any())).willReturn(channelDto);

    // when
    ChannelDto result = channelService.create(req);

    // then
    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
    then(channelRepository).should().save(any(Channel.class));
  }

  // ── create PRIVATE ────────────────────────────────────────────

  @Test
  @DisplayName("PRIVATE 채널 생성 성공")
  void createPrivate_success() {
    // given
    UUID participantId = UUID.randomUUID();
    PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of(participantId));
    given(channelRepository.save(any(Channel.class))).willReturn(privateChannel);
    given(userRepository.findById(participantId)).willReturn(
        Optional.of(new com.sprint.mission.discodeit.entity.User("user1", "user1@email.com", "pass", null)));
    given(channelMapper.toDto(any(Channel.class), any(), any())).willReturn(
        new ChannelDto(channelId, ChannelType.PRIVATE, null, null, List.of(), null));

    // when
    ChannelDto result = channelService.create(req);

    // then
    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
  }

  // ── update ────────────────────────────────────────────────────

  @Test
  @DisplayName("PUBLIC 채널 수정 성공")
  void update_success() {
    // given
    PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("updated", "Updated description");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));
    given(channelRepository.save(any(Channel.class))).willReturn(publicChannel);
    given(channelMapper.toDto(any(Channel.class), any(), any())).willReturn(channelDto);
    given(messageRepository.findAllByChannel_Id(any())).willReturn(List.of());

    // when
    ChannelDto result = channelService.update(channelId, req);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("PRIVATE 채널 수정 시 PrivateChannelUpdateException 발생")
  void update_privateChannel_throwsException() {
    // given
    PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("name", "desc");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

    // when / then
    assertThatThrownBy(() -> channelService.update(channelId, req))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  // ── delete ────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() {
    // given
    given(channelRepository.findById(channelId)).willReturn(Optional.of(publicChannel));

    // when
    channelService.delete(channelId);

    // then
    then(channelRepository).should().delete(publicChannel);
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 ChannelNotFoundException 발생")
  void delete_notFound_throwsException() {
    // given
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // ── findAllByUserId ────────────────────────────────────────────

  @Test
  @DisplayName("userId로 채널 목록 조회 - PUBLIC 채널 포함")
  void findAllByUserId_includesPublicChannels() {
    // given
    UUID userId = UUID.randomUUID();
    given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of());
    given(channelRepository.findPublicOrIn(any(), any())).willReturn(List.of(publicChannel));
    given(messageRepository.findLastMessageAtByChannelIds(any())).willReturn(List.of());
    given(readStatusRepository.findAllByChannel_IdIn(any())).willReturn(List.of());
    given(channelMapper.toDto(any(), any(), any())).willReturn(channelDto);

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    // PUBLIC 채널만 포함 (PRIVATE는 참여 중이 아님)
    assertThat(result).hasSize(1);
  }
}
