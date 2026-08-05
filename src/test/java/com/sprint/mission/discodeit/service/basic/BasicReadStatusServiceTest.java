package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
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
public class BasicReadStatusServiceTest {

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

  private UUID userId;
  private UUID channelId;
  private UUID userStatusId;
  private Instant lastReadAt;
  private String username;
  private String password;
  private String email;
  private User user;
  private String channelName;
  private String description;
  private Channel channel;
  private ReadStatus readStatus;
  private ReadStatusDto readStatusDto;

  @BeforeEach
  public void setUp() {
    userId = UuidCreator.getTimeOrderedEpoch();
    channelId = UuidCreator.getTimeOrderedEpoch();
    userStatusId = UuidCreator.getTimeOrderedEpoch();
    lastReadAt = Instant.now().minusSeconds(5);
    username = "testUser";
    password = "password1234!";
    email = "test@test.com";
    user = new User(username, email, password, null, Role.USER);
    ReflectionTestUtils.setField(user, "id", userId);
    channelName = "Test Channel";
    description = "This is a test channel.";
    channel = new Channel(ChannelType.PUBLIC, channelName, description);
    ReflectionTestUtils.setField(channel, "id", channelId);
    readStatus = ReadStatus.builder()
        .user(user)
        .channel(channel)
        .lastReadAt(lastReadAt)
        .build();
    ReflectionTestUtils.setField(readStatus, "id", userStatusId);
    readStatusDto = new ReadStatusDto(userStatusId, userId, channelId, lastReadAt);
  }

  // ── create ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 읽음 상태 생성 성공")
  public void create() {
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).willReturn(
        Optional.empty());
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(readStatusDto);

    ReadStatusDto result = readStatusService.create(request);

    assertThat(result).isEqualTo(readStatusDto);
    verify(readStatusRepository).save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("채널 읽음 상태 생성 (실패 - 사용자 없음)")
  public void create_userNotFound() {
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> readStatusService.create(request))
        .isInstanceOf(UserNotFoundException.class);
    verify(userRepository).findById(any());
    verify(readStatusRepository, never()).save(any());
  }

  @Test
  @DisplayName("채널 읽음 상태 생성 (실패 - 채널 없음)")
  public void create_channelNotFound() {
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> readStatusService.create(request))
        .isInstanceOf(ChannelNotFoundException.class);
    verify(userRepository).findById(any());
    verify(channelRepository).findById(any());
    verify(readStatusRepository, never()).save(any());
  }

  @Test
  @DisplayName("채널 읽음 상태 생성 (실패 - 이미 존재)")
  public void create_alreadyExists() {
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).willReturn(
        Optional.of(readStatus));

    assertThatThrownBy(() -> readStatusService.create(request))
        .isInstanceOf(ReadStatusAlreadyExistException.class);
    verify(readStatusRepository).findByUserIdAndChannelId(any(), any());
    verify(readStatusRepository, never()).save(any());
  }

  // ── find ────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 읽음 상태 조회 (성공)")
  public void find() {
    given(readStatusRepository.findById(userStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(readStatus)).willReturn(readStatusDto);

    ReadStatusDto result = readStatusService.find(userStatusId);

    assertThat(result).isEqualTo(readStatusDto);
  }

  @Test
  @DisplayName("채널 읽음 상태 조회 (실패 - 상태 없음)")
  public void find_notFound() {
    given(readStatusRepository.findById(userStatusId)).willReturn(Optional.empty());
    assertThatThrownBy(() -> readStatusService.find(userStatusId))
        .isInstanceOf(ReadStatusNotFoundException.class);
    verify(readStatusRepository).findById(any());
  }

  // ── update ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 읽음 수정 (성공)")
  public void update() {
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());
    given(readStatusRepository.findById(userStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(readStatusDto);

    ReadStatusDto result = readStatusService.update(userStatusId, request);

    assertThat(result).isEqualTo(readStatusDto);
  }

  @Test
  @DisplayName("채널 읽음 수정 (실패 - 상태 없음)")
  public void update_notFound() {
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());
    given(readStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> readStatusService.update(userStatusId, request))
        .isInstanceOf(ReadStatusNotFoundException.class);

    verify(readStatusRepository).findById(any());
    verify(readStatusRepository, never()).save(any());
  }

  // ── delete ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 읽음 상태 삭제 (성공)")
  public void delete() {
    given(readStatusRepository.existsById(userStatusId)).willReturn(true);

    readStatusService.delete(userStatusId);

    verify(readStatusRepository).deleteById(userStatusId);
  }

  @Test
  @DisplayName("채널 읽음 상태 삭제 (실패 - 상태 없음)")
  public void delete_notFound() {
    given(readStatusRepository.existsById(userStatusId)).willReturn(false);

    assertThatThrownBy(() -> readStatusService.delete(userStatusId))
        .isInstanceOf(ReadStatusNotFoundException.class);

    verify(readStatusRepository, never()).deleteById(userStatusId);
  }
}
