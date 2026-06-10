package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicReadStatusServiceTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusMapper readStatusMapper;

  @InjectMocks
  private BasicReadStatusService readStatusService;

  private UUID readStatusId;
  private UUID userId;
  private UUID channelId;
  private User user;
  private Channel channel;
  private ReadStatus readStatus;
  private ReadStatusDto readStatusDto;

  @BeforeEach
  void setUp() {
    readStatusId = UUID.randomUUID();
    userId = UUID.randomUUID();
    channelId = UUID.randomUUID();

    user = new User("testUser", "test@example.com", "test1234", null);
    ReflectionTestUtils.setField(user, "id", userId);

    channel = new Channel(ChannelType.PUBLIC, "테스트채널", null);
    ReflectionTestUtils.setField(channel, "id", channelId);

    readStatus = new ReadStatus(user, channel, Instant.now());
    ReflectionTestUtils.setField(readStatus, "id", readStatusId);

    readStatusDto = new ReadStatusDto(readStatusId, userId, channelId, Instant.now());
  }

  // ── create ──────────────────────────────────────────────

  @Test
  @DisplayName("ReadStatus 생성 성공")
  void createReadStatus_Success() {
    // given
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(eq(userId), eq(channelId)))
        .willReturn(Optional.empty());
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(readStatusDto);

    // when
    ReadStatusDto result = readStatusService.create(request);

    // then
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.channelId()).isEqualTo(channelId);
    verify(readStatusRepository).save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("존재하지 않는 유저로 ReadStatus 생성 시 실패")
  void createReadStatus_UserNotFound_ThrowsException() {
    // given
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> readStatusService.create(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("존재하지 않는 채널로 ReadStatus 생성 시 실패")
  void createReadStatus_ChannelNotFound_ThrowsException() {
    // given
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> readStatusService.create(request))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("이미 존재하는 ReadStatus 생성 시 실패")
  void createReadStatus_Duplicate_ThrowsException() {
    // given
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(eq(userId), eq(channelId)))
        .willReturn(Optional.of(readStatus));

    // when & then
    assertThatThrownBy(() -> readStatusService.create(request))
        .isInstanceOf(DuplicateReadStatusException.class);
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("ReadStatus 수정 성공")
  void updateReadStatus_Success() {
    // given
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());
    given(readStatusRepository.findById(eq(readStatusId))).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(readStatusDto);

    // when
    ReadStatusDto result = readStatusService.update(readStatusId, request);

    // then
    assertThat(result).isEqualTo(readStatusDto);
  }

  @Test
  @DisplayName("존재하지 않는 ReadStatus 수정 시 실패")
  void updateReadStatus_NotFound_ThrowsException() {
    // given
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());
    given(readStatusRepository.findById(eq(readStatusId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> readStatusService.update(readStatusId, request))
        .isInstanceOf(ReadStatusNotFoundException.class);
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("ReadStatus 삭제 성공")
  void deleteReadStatus_Success() {
    // given
    given(readStatusRepository.existsById(eq(readStatusId))).willReturn(true);

    // when
    readStatusService.delete(readStatusId);

    // then
    verify(readStatusRepository).deleteById(readStatusId);
  }

  @Test
  @DisplayName("존재하지 않는 ReadStatus 삭제 시 실패")
  void deleteReadStatus_NotFound_ThrowsException() {
    // given
    given(readStatusRepository.existsById(eq(readStatusId))).willReturn(false);

    // when & then
    assertThatThrownBy(() -> readStatusService.delete(readStatusId))
        .isInstanceOf(ReadStatusNotFoundException.class);
  }

  // ── findAllByUserId ──────────────────────────────────────

  @Test
  @DisplayName("userId로 ReadStatus 목록 조회 성공")
  void findAllByUserId_Success() {
    // given
    given(readStatusRepository.findAllByUserId(eq(userId))).willReturn(List.of(readStatus));
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(readStatusDto);

    // when
    List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).userId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("ReadStatus 없으면 빈 목록 반환")
  void findAllByUserId_Empty_ReturnsEmpty() {
    // given
    given(readStatusRepository.findAllByUserId(eq(userId))).willReturn(List.of());

    // when
    List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

    // then
    assertThat(result).isEmpty();
  }
}