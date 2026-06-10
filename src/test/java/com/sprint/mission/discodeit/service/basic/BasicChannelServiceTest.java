package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  private UUID channelId;
  private Channel publicChannel;
  private Channel privateChannel;
  private ChannelDto channelDto;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();

    publicChannel = new Channel(ChannelType.PUBLIC, "공지사항", "공지 채널입니다");
    ReflectionTestUtils.setField(publicChannel, "id", channelId);

    privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    ReflectionTestUtils.setField(privateChannel, "id", channelId);

    channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "공지사항", "공지 채널입니다", List.of(), null);
  }

  // ── create PUBLIC ────────────────────────────────────────

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void createPublicChannel_Success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지사항", "공지 채널입니다");
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("PUBLIC 채널 생성 시 name, description 저장됨")
  void createPublicChannel_NameAndDescriptionSaved() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지사항", "공지 채널입니다");
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result.name()).isEqualTo("공지사항");
  }

  // ── create PRIVATE ───────────────────────────────────────

  @Test
  @DisplayName("PRIVATE 채널 생성 성공")
  void createPrivateChannel_Success() {
    // given
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
    given(userRepository.findAllById(participantIds)).willReturn(List.of());
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    channelService.create(request);

    // then
    verify(channelRepository).save(any(Channel.class));
    verify(readStatusRepository).saveAll(any());
  }

  @Test
  @DisplayName("PRIVATE 채널 생성 시 참가자 없으면 ReadStatus 저장 안됨")
  void createPrivateChannel_WithNoParticipants_EmptyReadStatus() {
    // given
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of());
    given(userRepository.findAllById(List.of())).willReturn(List.of());
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    channelService.create(request);

    // then
    verify(readStatusRepository).saveAll(List.of());
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("PUBLIC 채널 수정 성공")
  void updateChannel_Success() {
    // given
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(publicChannel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    ChannelDto result = channelService.update(channelId, request);

    // then
    assertThat(result).isEqualTo(channelDto);
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 시 실패")
  void updateChannel_WithNonExistentId_ThrowsException() {
    // given
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("PRIVATE 채널 수정 시도 시 실패")
  void updateChannel_PrivateChannel_ThrowsException() {
    // given
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새이름", "새설명");
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(privateChannel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_Success() {
    // given
    given(channelRepository.existsById(eq(channelId))).willReturn(true);

    // when
    channelService.delete(channelId);

    // then
    verify(messageRepository).deleteAllByChannelId(channelId);
    verify(readStatusRepository).deleteAllByChannelId(channelId);
    verify(channelRepository).deleteById(channelId);
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 실패")
  void deleteChannel_WithNonExistentId_ThrowsException() {
    // given
    given(channelRepository.existsById(eq(channelId))).willReturn(false);

    // when & then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // ── findAllByUserId ──────────────────────────────────────

  @Test
  @DisplayName("userId로 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    // given
    UUID userId = UUID.randomUUID();
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), any()))
        .willReturn(List.of(publicChannel));
    given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("채널이 없으면 빈 목록 반환")
  void findAllByUserId_NoneExist_ReturnsEmpty() {
    // given
    UUID userId = UUID.randomUUID();
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), any()))
        .willReturn(List.of());

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).isEmpty();
  }
}