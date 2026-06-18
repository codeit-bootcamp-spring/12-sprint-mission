package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceMockTest {

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

	private PublicChannelCreateRequest createPublicChannelCreateRequest() {
		return new PublicChannelCreateRequest("일반 채널", "자유롭게 대화하는 채널입니다.");
	}

	private PrivateChannelCreateRequest createPrivateChannelCreateRequest(List<UUID> participantIds) {
		return new PrivateChannelCreateRequest(participantIds);
	}

	private PublicChannelUpdateRequest createPublicChannelUpdateRequest() {
		return new PublicChannelUpdateRequest("수정된 채널명", "수정된 설명입니다.");
	}

	private User createMockUser(UUID userId) {
		return new User("user", "user@example.com", "password", null);
	}


	@Test
	@DisplayName("Create Public - 성공: 공개 채널을 정상 생성한다")
	void create_Public_Success() {
		// given
		PublicChannelCreateRequest request = createPublicChannelCreateRequest();

		given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(channelMapper.toDto(any(Channel.class)))
			.willReturn(new ChannelDto(
				UUID.randomUUID(),
				ChannelType.PUBLIC,
				request.name(),
				request.description(),
				List.of(),
				Instant.now()
			));

		// when
		ChannelDto result = channelService.create(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
		assertThat(result.name()).isEqualTo(request.name());

		then(channelRepository).should(times(1)).save(any(Channel.class));
	}

	@Test
	@DisplayName("Create Private - 성공: 참가자들을 포함한 비공개 채널을 생성하고 읽기 상태를 저장한다")
	void create_Private_Success() {
		// given
		List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
		PrivateChannelCreateRequest request = createPrivateChannelCreateRequest(participantIds);
		List<User> mockUsers = List.of(createMockUser(participantIds.get(0)), createMockUser(participantIds.get(1)));

		given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(userRepository.findAllById(request.participantIds())).willReturn(mockUsers);
		given(channelMapper.toDto(any(Channel.class)))
			.willReturn(new ChannelDto(
				UUID.randomUUID(),
				ChannelType.PRIVATE,
				null,
				null,
				List.of(),
				Instant.now()
			));

		// when
		ChannelDto result = channelService.create(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
		assertThat(result.name()).isNull();

		then(channelRepository).should(times(1)).save(any(Channel.class));
		then(userRepository).should(times(1)).findAllById(request.participantIds());
		then(readStatusRepository).should(times(1)).saveAll(anyList());
	}

	@Test
	@DisplayName("Create Public - 실패: 데이터베이스 저장 중 예외가 발생하면 채널 생성에 실패한다")
	void create_Public_Fail_DatabaseException() {
		// given
		PublicChannelCreateRequest request = createPublicChannelCreateRequest();

		given(channelRepository.save(any(Channel.class)))
			.willThrow(new IllegalArgumentException("이미 존재하는 채널명입니다."));

		// when & then
		assertThatThrownBy(() -> channelService.create(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("이미 존재하는 채널명입니다.");

		then(channelMapper).should(never()).toDto(any());
	}

	@Test
	@DisplayName("Create Private - 실패: 참여자 ID 목록에 해당하는 유저가 존재하지 않으면 읽기 상태를 저장하지 않는다")
	void create_Private_Fail_ParticipantsNotFound() {
		// given
		List<UUID> invalidParticipantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
		PrivateChannelCreateRequest request = createPrivateChannelCreateRequest(invalidParticipantIds);

		given(channelRepository.save(any(Channel.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(userRepository.findAllById(request.participantIds())).willReturn(List.of());
		given(channelMapper.toDto(any(Channel.class)))
			.willReturn(new ChannelDto(
				UUID.randomUUID(),
				ChannelType.PRIVATE,
				null,
				null,
				List.of(),
				Instant.now()
			));

		// when
		ChannelDto result = channelService.create(request);

		// then
		assertThat(result).isNotNull();
		then(readStatusRepository).should(times(1)).saveAll(eq(List.of()));
	}


	@Test
	@DisplayName("Update - 성공: 공개 채널의 이름과 설명을 수정한다")
	void update_PublicChannel_Success() {
		// given
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest request = createPublicChannelUpdateRequest();
		Channel existingPublicChannel = new Channel(ChannelType.PUBLIC, "구채널명", "구설명");

		given(channelRepository.findById(channelId)).willReturn(Optional.of(existingPublicChannel));
		given(channelMapper.toDto(any(Channel.class)))
			.willReturn(new ChannelDto(
				channelId,
				ChannelType.PUBLIC,
				request.newName(),
				request.newDescription(),
				List.of(),
				Instant.now()
			));

		// when
		ChannelDto result = channelService.update(channelId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo(request.newName());
		assertThat(result.description()).isEqualTo(request.newDescription());
	}

	@Test
	@DisplayName("Update - 실패: 존재하지 않는 채널을 수정하려고 하면 예외가 발생한다")
	void update_ThrowsException_WhenChannelNotFound() {
		// given
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest request = createPublicChannelUpdateRequest();

		given(channelRepository.findById(channelId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> channelService.update(channelId, request))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@DisplayName("Update - 실패: 비공개 채널을 수정하려고 하면 예외가 발생한다")
	void update_ThrowsException_WhenChannelIsPrivate() {
		// given
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest request = createPublicChannelUpdateRequest();
		Channel existingPrivateChannel = new Channel(ChannelType.PRIVATE, null, null); // PRIVATE 채널

		given(channelRepository.findById(channelId)).willReturn(Optional.of(existingPrivateChannel));

		// when & then
		assertThatThrownBy(() -> channelService.update(channelId, request))
			.isInstanceOf(PrivateChannelUpdateException.class);
	}


	@Test
	@DisplayName("Delete - 성공: 해당 채널과 관련된 메시지, 읽기 상태를 연쇄 삭제한다")
	void delete_Success() {
		// given
		UUID channelId = UUID.randomUUID();
		given(channelRepository.existsById(channelId)).willReturn(true);

		// when
		channelService.delete(channelId);

		// then
		then(messageRepository).should(times(1)).deleteAllByChannelId(channelId);
		then(readStatusRepository).should(times(1)).deleteAllByChannelId(channelId);
		then(channelRepository).should(times(1)).deleteById(channelId);
	}

	@Test
	@DisplayName("Delete - 실패: 존재하지 않는 채널을 삭제하려고 하면 예외가 발생한다")
	void delete_ThrowsException_WhenChannelNotFound() {
		// given
		UUID channelId = UUID.randomUUID();
		given(channelRepository.existsById(channelId)).willReturn(false);

		// when & then
		assertThatThrownBy(() -> channelService.delete(channelId))
			.isInstanceOf(ChannelNotFoundException.class);

		then(channelRepository).should(never()).deleteById(any());
	}

	@Test
	@DisplayName("FindAllByUserId - 성공: 유저가 속한 비공개 채널과 모든 공개 채널 목록을 조회한다")
	void findAllByUserId_Success() {
		// given
		UUID userId = UUID.randomUUID();
		UUID privateChannelId = UUID.randomUUID();

		ReadStatus mockReadStatus = mock(ReadStatus.class);
		Channel mockPrivateChannel = mock(Channel.class);

		given(mockReadStatus.getChannel()).willReturn(mockPrivateChannel);
		given(mockPrivateChannel.getId()).willReturn(privateChannelId);
		given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(mockReadStatus));

		Channel publicChannel = new Channel(ChannelType.PUBLIC, "광장", "모두의 광장");
		given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(privateChannelId)))
			.willReturn(List.of(publicChannel, mockPrivateChannel));

		given(channelMapper.toDto(any(Channel.class))).willAnswer(invocation -> {
			Channel source = invocation.getArgument(0);
			return new ChannelDto(
				UUID.randomUUID(),
				source.getType(),
				source.getName(),
				source.getDescription(),
				List.of(),
				Instant.now()
			);
		});

		// when
		List<ChannelDto> result = channelService.findAllByUserId(userId);

		// then
		assertThat(result).hasSize(2);
		then(readStatusRepository).should(times(1)).findAllByUserId(userId);
		then(channelRepository).should(times(1)).findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(privateChannelId));
	}

	@Test
	@DisplayName("FindAllByUserId - 실패/조회결과없음: 참여 중인 채널 및 공개 채널이 없으면 빈 리스트를 반환한다")
	void findAllByUserId_ReturnsEmptyList_WhenNoChannelsExist() {
		// given
		UUID userId = UUID.randomUUID();

		given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
		given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of()))
			.willReturn(List.of());

		// when
		List<ChannelDto> result = channelService.findAllByUserId(userId);

		// then
		assertThat(result).isEmpty();
		then(readStatusRepository).should(times(1)).findAllByUserId(userId);
		then(channelRepository).should(times(1)).findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());
		then(channelMapper).should(never()).toDto(any());
	}
}