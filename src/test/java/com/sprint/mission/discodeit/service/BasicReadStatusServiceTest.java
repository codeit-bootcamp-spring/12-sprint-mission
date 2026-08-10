package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
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
class BasicReadStatusServiceTest {

  @Mock ReadStatusRepository readStatusRepository;
  @Mock UserRepository userRepository;
  @Mock ChannelRepository channelRepository;
  @Mock ReadStatusMapper readStatusMapper;

  @InjectMocks BasicReadStatusService readStatusService;

  private UUID userId;
  private UUID channelId;
  private UUID readStatusId;
  private User user;
  private Channel channel;
  private ReadStatus readStatus;
  private ReadStatusDto readStatusDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    readStatusId = UUID.randomUUID();
    user = new User("testuser", "test@email.com", "password123!", null);
    channel = new Channel(ChannelType.PUBLIC, "general", null);
    readStatus = new ReadStatus(user, channel, Instant.now());
    readStatusDto = new ReadStatusDto(readStatusId, userId, channelId, Instant.now(), false);
  }

  @Test
  @DisplayName("ReadStatus 생성 성공")
  void create_success() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)).willReturn(Optional.empty());
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
    given(readStatusMapper.toDto(readStatus)).willReturn(readStatusDto);

    ReadStatusDto result = readStatusService.create(new ReadStatusCreateRequest(userId, channelId, Instant.now()));

    assertThat(result).isNotNull();
    then(readStatusRepository).should().save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("이미 존재하는 ReadStatus 조회 시 기존 값 반환")
  void create_alreadyExists_returnsExisting() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(readStatus)).willReturn(readStatusDto);

    ReadStatusDto result = readStatusService.create(new ReadStatusCreateRequest(userId, channelId, Instant.now()));

    assertThat(result).isEqualTo(readStatusDto);
    then(readStatusRepository).should(org.mockito.Mockito.never()).save(any());
  }

  @Test
  @DisplayName("ReadStatus 단건 조회 성공")
  void find_success() {
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(readStatus)).willReturn(readStatusDto);

    ReadStatusDto result = readStatusService.find(readStatusId);

    assertThat(result).isEqualTo(readStatusDto);
  }

  @Test
  @DisplayName("존재하지 않는 ReadStatus 조회 시 예외 발생")
  void find_notFound_throwsException() {
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> readStatusService.find(readStatusId))
        .isInstanceOf(ReadStatusNotFoundException.class);
  }

  @Test
  @DisplayName("userId로 ReadStatus 목록 조회 성공")
  void findAllByUserId_success() {
    given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of(readStatus));
    given(readStatusMapper.toDto(readStatus)).willReturn(readStatusDto);

    List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("ReadStatus 수정 성공")
  void update_success() {
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusRepository.save(readStatus)).willReturn(readStatus);
    given(readStatusMapper.toDto(readStatus)).willReturn(readStatusDto);

    ReadStatusDto result = readStatusService.update(readStatusId, new ReadStatusUpdateRequest(Instant.now(), null));

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("ReadStatus 삭제 성공")
  void delete_success() {
    given(readStatusRepository.existsById(readStatusId)).willReturn(true);

    readStatusService.delete(readStatusId);

    then(readStatusRepository).should().deleteById(readStatusId);
  }
}
