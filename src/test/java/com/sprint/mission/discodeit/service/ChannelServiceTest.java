package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

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
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
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

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublic_success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "공지사항",
        "공지 채널입니다."
    );

    ChannelDto response = mock(ChannelDto.class);

    given(channelRepository.save(any(Channel.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    given(channelMapper.toDto(any(Channel.class)))
        .willReturn(response);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isSameAs(response);

    then(channelRepository).should().save(any(Channel.class));
    then(channelMapper).should().toDto(any(Channel.class));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 - 저장 중 예외 발생")
  void createPublic_fail_repositoryException() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "공지사항",
        "공지 채널입니다."
    );

    given(channelRepository.save(any(Channel.class)))
        .willThrow(new RuntimeException("채널 저장 실패"));

    // when & then
    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("채널 저장 실패");

    then(channelRepository).should().save(any(Channel.class));
    then(channelMapper).should(never()).toDto(any(Channel.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivate_success() {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(userId1, userId2)
    );

    User user1 = new User("user1", "user1@test.com", "1234", null);
    User user2 = new User("user2", "user2@test.com", "1234", null);

    ChannelDto response = mock(ChannelDto.class);

    given(channelRepository.save(any(Channel.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    given(userRepository.findAllById(request.participantIds()))
        .willReturn(List.of(user1, user2));

    given(readStatusRepository.saveAll(anyList()))
        .willAnswer(invocation -> invocation.getArgument(0));

    given(channelMapper.toDto(any(Channel.class)))
        .willReturn(response);

    // when
    ChannelDto result = channelService.create(request);

    // then
    assertThat(result).isSameAs(response);

    then(channelRepository).should().save(any(Channel.class));
    then(userRepository).should().findAllById(request.participantIds());
    then(readStatusRepository).should().saveAll(anyList());
    then(channelMapper).should().toDto(any(Channel.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - 참여자 조회 중 예외 발생")
  void createPrivate_fail_repositoryException() {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(userId1, userId2)
    );

    given(channelRepository.save(any(Channel.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    given(userRepository.findAllById(request.participantIds()))
        .willThrow(new RuntimeException("참여자 조회 실패"));

    // when & then
    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("참여자 조회 실패");

    then(channelRepository).should().save(any(Channel.class));
    then(userRepository).should().findAllById(request.participantIds());
    then(readStatusRepository).should(never()).saveAll(anyList());
    then(channelMapper).should(never()).toDto(any(Channel.class));
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_success() {
    // given
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
        "수정된 채널",
        "수정된 설명"
    );

    Channel channel = new Channel(
        ChannelType.PUBLIC,
        "기존 채널",
        "기존 설명"
    );

    ChannelDto response = mock(ChannelDto.class);

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));

    given(channelMapper.toDto(channel))
        .willReturn(response);

    // when
    ChannelDto result = channelService.update(channelId, request);

    // then
    assertThat(result).isSameAs(response);

    then(channelRepository).should().findById(channelId);
    then(channelMapper).should().toDto(channel);
  }

  @Test
  @DisplayName("채널 수정 실패 - 비공개 채널 수정 불가")
  void update_fail_privateChannel() {
    // given
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
        "수정된 채널",
        "수정된 설명"
    );

    Channel channel = new Channel(
        ChannelType.PRIVATE,
        null,
        null
    );

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);

    then(channelRepository).should().findById(channelId);
    then(channelMapper).should(never()).toDto(any(Channel.class));
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() {
    // given
    UUID channelId = UUID.randomUUID();

    given(channelRepository.existsById(channelId))
        .willReturn(true);

    // when
    channelService.delete(channelId);

    // then
    then(channelRepository).should().existsById(channelId);
    then(messageRepository).should().deleteAllByChannelId(channelId);
    then(readStatusRepository).should().deleteAllByChannelId(channelId);
    then(channelRepository).should().deleteById(channelId);
  }

  @Test
  @DisplayName("채널 삭제 실패 - 채널 없음")
  void delete_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();

    given(channelRepository.existsById(channelId))
        .willReturn(false);

    // when & then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should().existsById(channelId);
    then(messageRepository).should(never()).deleteAllByChannelId(channelId);
    then(readStatusRepository).should(never()).deleteAllByChannelId(channelId);
    then(channelRepository).should(never()).deleteById(channelId);
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 성공")
  void findAllByUserId_success() {
    // given
    UUID userId = UUID.randomUUID();

    Channel privateChannel = new Channel(
        ChannelType.PRIVATE,
        null,
        null
    );

    User user = new User("minji", "minji@test.com", "1234", null);
    ReadStatus readStatus = new ReadStatus(user, privateChannel, privateChannel.getCreatedAt());

    Channel publicChannel = new Channel(
        ChannelType.PUBLIC,
        "공지사항",
        "공지 채널입니다."
    );

    ChannelDto response = mock(ChannelDto.class);

    given(readStatusRepository.findAllByUserId(userId))
        .willReturn(List.of(readStatus));

    given(channelRepository.findAllByTypeOrIdIn(
        eq(ChannelType.PUBLIC),
        anyList()
    )).willReturn(List.of(publicChannel, privateChannel));

    given(channelMapper.toDto(any(Channel.class)))
        .willReturn(response);

    // when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(2);

    then(readStatusRepository).should().findAllByUserId(userId);
    then(channelRepository).should().findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
    then(channelMapper).should().toDto(publicChannel);
    then(channelMapper).should().toDto(privateChannel);
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 실패 - 조회 중 예외 발생")
  void findAllByUserId_fail_repositoryException() {
    // given
    UUID userId = UUID.randomUUID();

    given(readStatusRepository.findAllByUserId(userId))
        .willThrow(new RuntimeException("읽음 상태 조회 실패"));

    // when & then
    assertThatThrownBy(() -> channelService.findAllByUserId(userId))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("읽음 상태 조회 실패");

    then(readStatusRepository).should().findAllByUserId(userId);
    then(channelRepository).should(never()).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
  }
}