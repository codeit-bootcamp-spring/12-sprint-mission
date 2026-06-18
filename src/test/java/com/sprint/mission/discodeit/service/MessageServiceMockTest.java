package com.sprint.mission.discodeit.service;

import java.time.Instant;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceMockTest {

	@InjectMocks
	private BasicMessageService messageService;

	@Mock
	private MessageRepository messageRepository;
	@Mock
	private ChannelRepository channelRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private MessageMapper messageMapper;
	@Mock
	private BinaryContentStorage binaryContentStorage;
	@Mock
	private BinaryContentRepository binaryContentRepository;
	@Mock
	private PageResponseMapper pageResponseMapper;

	private MessageCreateRequest createMessageCreateRequest(UUID channelId, UUID authorId) {
		return new MessageCreateRequest("안녕하세요! 반갑습니다.", channelId, authorId);
	}

	private MessageUpdateRequest createMessageUpdateRequest() {
		return new MessageUpdateRequest("수정된 메시지 내용입니다.");
	}

	private BinaryContentCreateRequest createAttachmentRequest() {
		return new BinaryContentCreateRequest("image.png", "image/png", new byte[]{1, 2, 3});
	}


	@Test
	@DisplayName("Create - 성공: 첨부파일이 포함된 메시지를 정상적으로 생성한다")
	void create_Success_WithAttachments() {
		// given
		UUID channelId = UUID.randomUUID();
		UUID authorId = UUID.randomUUID();
		MessageCreateRequest request = createMessageCreateRequest(channelId, authorId);
		BinaryContentCreateRequest attachmentRequest = createAttachmentRequest();

		Channel mockChannel = mock(Channel.class);
		User mockAuthor = mock(User.class);
		UserDto mockAuthorDto = mock(UserDto.class);

		MessageDto expectedMessageDto = new MessageDto(
			UUID.randomUUID(),
			Instant.now(),
			Instant.now(),
			request.content(),
			channelId,
			mockAuthorDto,
			List.of() // attachments
		);

		given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
		given(userRepository.findById(authorId)).willReturn(Optional.of(mockAuthor));
		given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(messageRepository.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(messageMapper.toDto(any(Message.class))).willReturn(expectedMessageDto);

		// when
		MessageDto result = messageService.create(request, List.of(attachmentRequest));

		// then
		assertThat(result).isNotNull();
		assertThat(result.content()).isEqualTo(request.content());
		assertThat(result.channelId()).isEqualTo(channelId);

		then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
		then(binaryContentStorage).should(times(1)).put(any(), any(byte[].class));
		then(messageRepository).should(times(1)).save(any(Message.class));
	}

	@Test
	@DisplayName("Create - 실패: 존재하지 않는 채널 ID로 메시지를 생성하면 예외가 발생한다")
	void create_Fail_ChannelNotFound() {
		// given
		UUID invalidChannelId = UUID.randomUUID();
		UUID authorId = UUID.randomUUID();
		MessageCreateRequest request = createMessageCreateRequest(invalidChannelId, authorId);

		given(channelRepository.findById(invalidChannelId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> messageService.create(request, List.of()))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessageContaining("Channel with id " + invalidChannelId + " does not exist");

		then(messageRepository).should(never()).save(any());
	}


	@Test
	@DisplayName("Update - 성공: 존재하는 메시지의 내용을 수정한다")
	void update_Success() {
		// given
		UUID messageId = UUID.randomUUID();
		UUID channelId = UUID.randomUUID();
		MessageUpdateRequest request = createMessageUpdateRequest();
		Message mockMessage = mock(Message.class);
		UserDto mockAuthorDto = mock(UserDto.class);

		MessageDto updatedMessageDto = new MessageDto(
			messageId,
			Instant.now(),
			Instant.now(),
			request.newContent(),
			channelId,
			mockAuthorDto,
			List.of()
		);

		given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));
		given(messageMapper.toDto(mockMessage)).willReturn(updatedMessageDto);

		// when
		MessageDto result = messageService.update(messageId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.content()).isEqualTo(request.newContent());
		then(mockMessage).should(times(1)).update(request.newContent());
	}

	@Test
	@DisplayName("Update - 실패: 존재하지 않는 메시지를 수정하려고 하면 예외가 발생한다")
	void update_Fail_MessageNotFound() {
		// given
		UUID invalidMessageId = UUID.randomUUID();
		MessageUpdateRequest request = createMessageUpdateRequest();

		given(messageRepository.findById(invalidMessageId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> messageService.update(invalidMessageId, request))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessageContaining("Message with id " + invalidMessageId + " not found");
	}


	@Test
	@DisplayName("Delete - 성공: 존재하는 메시지를 정상 삭제한다")
	void delete_Success() {
		// given
		UUID messageId = UUID.randomUUID();
		given(messageRepository.existsById(messageId)).willReturn(true);

		// when
		messageService.delete(messageId);

		// then
		then(messageRepository).should(times(1)).deleteById(messageId);
	}

	@Test
	@DisplayName("Delete - 실패: 존재하지 않는 메시지를 삭제하려고 하면 예외가 발생한다")
	void delete_Fail_MessageNotFound() {
		// given
		UUID invalidMessageId = UUID.randomUUID();
		given(messageRepository.existsById(invalidMessageId)).willReturn(false);

		// when & then
		assertThatThrownBy(() -> messageService.delete(invalidMessageId))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessageContaining("Message with id " + invalidMessageId + " not found");

		then(messageRepository).should(never()).deleteById(any());
	}


	@Test
	@DisplayName("FindAllByChannelId - 성공: 특정 채널의 메시지 목록을 커서 기반 페이징으로 조회한다")
	void findAllByChannelId_Success() {
		// given
		UUID channelId = UUID.randomUUID();
		Instant createdAt = Instant.now();
		Pageable pageable = PageRequest.of(0, 10);
		UserDto mockAuthorDto = mock(UserDto.class);

		MessageDto messageDto1 = new MessageDto(UUID.randomUUID(), createdAt.minusSeconds(10), createdAt.minusSeconds(10), "첫 메시지", channelId, mockAuthorDto, List.of());
		MessageDto messageDto2 = new MessageDto(UUID.randomUUID(), createdAt, createdAt, "두 번째 메시지", channelId, mockAuthorDto, List.of());

		Slice<Message> mockSlice = new SliceImpl<>(List.of(mock(Message.class), mock(Message.class)), pageable, false);

		given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable))).willReturn(mockSlice);
		given(messageMapper.toDto(any(Message.class))).willReturn(messageDto1, messageDto2);

		PageResponse<MessageDto> expectedPageResponse = mock(PageResponse.class);
		given(pageResponseMapper.fromSlice(any(Slice.class), eq(messageDto2.createdAt()))).willReturn(expectedPageResponse);

		// when
		PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, createdAt, pageable);

		// then
		assertThat(result).isNotNull();
		then(messageRepository).should(times(1)).findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable));
		then(pageResponseMapper).should(times(1)).fromSlice(any(Slice.class), eq(messageDto2.createdAt()));
	}

	@Test
	@DisplayName("FindAllByChannelId - 실패/결과없음: 채널에 메시지가 전혀 없으면 nextCursor를 null로 매핑하여 반환한다")
	void findAllByChannelId_ReturnsEmptyPage_WhenNoMessagesExist() {
		// given
		UUID channelId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);

		Slice<Message> emptySlice = new SliceImpl<>(List.of(), pageable, false);

		given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable))).willReturn(emptySlice);

		PageResponse<MessageDto> expectedEmptyResponse = mock(PageResponse.class);
		given(pageResponseMapper.fromSlice(any(Slice.class), eq(null))).willReturn(expectedEmptyResponse);

		// when
		PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

		// then
		assertThat(result).isNotNull();
		then(messageRepository).should(times(1)).findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable));
		then(pageResponseMapper).should(times(1)).fromSlice(any(Slice.class), eq(null));
		then(messageMapper).should(never()).toDto(any());
	}
}
