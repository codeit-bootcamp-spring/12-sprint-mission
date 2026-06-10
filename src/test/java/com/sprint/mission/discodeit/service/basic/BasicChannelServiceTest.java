package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
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
public class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  private UUID userId;
  private UUID channelId;
  private String name;
  private String description;
  private User user;
  private Channel channel;
  private ChannelDto channelDto;
  private Instant now;

  @BeforeEach
  public void setUp() {
    userId = UuidCreator.getTimeOrderedEpoch();
    channelId = UuidCreator.getTimeOrderedEpoch();
    name = "Test Channel";
    description = "This is a test channel.";
    now = Instant.now();
    user = new User("testUser", "test@test.com", "password1234!", null);
    channel = new Channel(ChannelType.PUBLIC, name, description);
    ReflectionTestUtils.setField(channel, "id", channelId);
    channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, name, description, List.of(), now);
  }

  // ── create ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("공개 채널 생성 테스트(성공)")
  public void createPublicChannel() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, description);
    given(channelRepository.save(any())).willReturn(channel);
    given(channelMapper.toDto(channel)).willReturn(channelDto);

    ChannelDto result = channelService.create(request);

    assertThat(result).isEqualTo(channelDto);
  }

  @Test
  @DisplayName("비공개 채널 생성 테스트(성공)")
  public void createPrivateChannel() {
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    ReflectionTestUtils.setField(privateChannel, "id", channelId);
    ChannelDto privateChannelDto = new ChannelDto(channelId, ChannelType.PRIVATE, null,
        null, List.of(), now);

    given(userRepository.findAllById(List.of(userId))).willReturn(List.of(user));
    given(channelRepository.save(any())).willReturn(privateChannel);
    given(channelMapper.toDto(privateChannel)).willReturn(privateChannelDto);

    ChannelDto result = channelService.create(request);

    assertThat(result).isEqualTo(privateChannelDto);
    verify(readStatusRepository).saveAll(anyList());
  }

  // ── findByUser ──────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 채널 조회 테스트(성공)")
  public void findByUser() {
    List<ReadStatus> readStatuses = List.of(new ReadStatus(user, channel, now));
    given(readStatusRepository.findAllByUserId(userId)).willReturn(readStatuses);
    given(channelRepository.findAllPublicOrId(List.of(channelId))).willReturn(List.of(channel));
    given(channelMapper.toDto(channel)).willReturn(channelDto);

    List<ChannelDto> result = channelService.findAllByUserId(userId);
    assertThat(result).containsExactly(channelDto);
  }

  // ── update ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 업데이트 테스트(성공)")
  public void update() {
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("Updated Name",
        "Updated Description");
    ChannelDto updatedChannelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "Updated Name",
        "Updated Description", List.of(), now);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelRepository.save(any())).willReturn(channel);
    given(channelMapper.toDto(channel)).willReturn(updatedChannelDto);

    ChannelDto result = channelService.update(channelId, request);

    assertThat(result).isEqualTo(updatedChannelDto);
  }

  @Test
  @DisplayName("채널 업데이트 테스트(실패 - 채널 없음)")
  public void update_notFound() {
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("Updated Name",
        "Updated Description");
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelNotFoundException.class);
    verify(channelRepository, never()).save(any());
  }

  @Test
  @DisplayName("채널 업데이트 테스트(실패 - 비공개 채널)")
  public void update_privateChannel() {
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("Updated Name",
        "Updated Description");
    ReflectionTestUtils.setField(channel, "type", ChannelType.PRIVATE);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);
    verify(channelRepository, never()).save(any());
  }

  // ── delete ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 삭제 테스트(성공)")
  public void delete() {
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    channelService.delete(channelId);

    verify(messageRepository).deleteAllByChannelId(channelId);
    verify(readStatusRepository).deleteAllByChannelId(channelId);
    verify(channelRepository).deleteById(channelId);
  }

  @Test
  @DisplayName("채널 삭제 테스트(실패 - 채널 없음)")
  public void delete_notFound() {
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }
}
