package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.service.BinaryContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentService binaryContentService;

    @InjectMocks
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 성공 - 첨부파일 없음")
    void create_success_withoutAttachments() {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                channelId,
                authorId
        );

        Channel channel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name("channel")
                .build();

        User author = User.builder()
                .id(authorId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        Message message = Message.builder()
                .content("hello")
                .channel(channel)
                .author(author)
                .attachments(List.of())
                .build();

        Message savedMessage = Message.builder()
                .id(messageId)
                .content("hello")
                .channel(channel)
                .author(author)
                .attachments(List.of())
                .build();

        MessageResponse expectedResponse = new MessageResponse(
                messageId,
                null,
                null,
                "hello",
                channelId,
                null,
                List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageMapper.toEntity(request, channel, author, List.of())).willReturn(message);
        given(messageRepository.save(message)).willReturn(savedMessage);
        given(messageMapper.toResponse(savedMessage)).willReturn(expectedResponse);

        MessageResponse result = messageService.create(request, List.of());

        assertThat(result).isEqualTo(expectedResponse);

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageMapper).should().toEntity(request, channel, author, List.of());
        then(messageRepository).should().save(message);
        then(messageMapper).should().toResponse(savedMessage);
        then(binaryContentService).should(never()).createBinaryContent(any());
    }

    @Test
    @DisplayName("메시지 생성 성공 - 첨부파일 있음")
    void create_success_withAttachments() {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                channelId,
                authorId
        );

        BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest(
                "file".getBytes(),
                "file.txt",
                "text/plain"
        );

        BinaryContent attachment = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName("file.txt")
                .size(4L)
                .contentType("text/plain")
                .build();

        Channel channel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name("channel")
                .build();

        User author = User.builder()
                .id(authorId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        Message message = Message.builder()
                .content("hello")
                .channel(channel)
                .author(author)
                .attachments(List.of(attachment))
                .build();

        Message savedMessage = Message.builder()
                .id(messageId)
                .content("hello")
                .channel(channel)
                .author(author)
                .attachments(List.of(attachment))
                .build();

        MessageResponse expectedResponse = new MessageResponse(
                messageId,
                null,
                null,
                "hello",
                channelId,
                null,
                List.of()
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(binaryContentService.createBinaryContent(attachmentRequest)).willReturn(attachment);
        given(messageMapper.toEntity(request, channel, author, List.of(attachment))).willReturn(message);
        given(messageRepository.save(message)).willReturn(savedMessage);
        given(messageMapper.toResponse(savedMessage)).willReturn(expectedResponse);

        MessageResponse result = messageService.create(request, List.of(attachmentRequest));

        assertThat(result).isEqualTo(expectedResponse);

        then(binaryContentService).should().createBinaryContent(attachmentRequest);
        then(messageMapper).should().toEntity(request, channel, author, List.of(attachment));
        then(messageRepository).should().save(message);
        then(messageMapper).should().toResponse(savedMessage);
    }

    @Test
    @DisplayName("메시지 생성 실패 - 채널 없음")
    void create_fail_channelNotFound() {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                channelId,
                authorId
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.create(request, List.of()))
                .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(userRepository).should(never()).findById(any());
        then(messageRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("메시지 생성 실패 - 작성자 없음")
    void create_fail_authorNotFound() {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                channelId,
                authorId
        );

        Channel channel = Channel.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name("channel")
                .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.create(request, List.of()))
                .isInstanceOf(UserNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("메시지 목록 조회 성공 - 첫 페이지, 다음 페이지 있음")
    void findAllByChannelId_success_firstPage_hasNext() {
        UUID channelId = UUID.randomUUID();

        Instant cursor1 = Instant.parse("2026-06-04T01:00:00Z");
        Instant cursor2 = Instant.parse("2026-06-04T00:59:00Z");
        Instant cursor3 = Instant.parse("2026-06-04T00:58:00Z");

        Message message1 = Message.builder()
                .id(UUID.randomUUID())
                .content("message1")
                .createdAt(cursor1)
                .build();

        Message message2 = Message.builder()
                .id(UUID.randomUUID())
                .content("message2")
                .createdAt(cursor2)
                .build();

        Message message3 = Message.builder()
                .id(UUID.randomUUID())
                .content("message3")
                .createdAt(cursor3)
                .build();

        MessageResponse response1 = new MessageResponse(
                message1.getId(),
                cursor1,
                cursor1,
                "message1",
                channelId,
                null,
                List.of()
        );

        MessageResponse response2 = new MessageResponse(
                message2.getId(),
                cursor2,
                cursor2,
                "message2",
                channelId,
                null,
                List.of()
        );

        Pageable pageable = Pageable.ofSize(2);

        given(messageRepository.findAllByChannel_IdOrderByCreatedAtDesc(
                any(UUID.class),
                any(Pageable.class)
        )).willReturn(List.of(message1, message2, message3));

        given(messageMapper.toResponse(message1)).willReturn(response1);
        given(messageMapper.toResponse(message2)).willReturn(response2);

        PageResponse<MessageResponse> result =
                messageService.findAllByChannelId(channelId, null, pageable);

        assertThat(result.content()).containsExactly(response1, response2);
        assertThat(result.nextCursor()).isEqualTo(cursor2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.hasNext()).isTrue();

        then(messageRepository).should().findAllByChannel_IdOrderByCreatedAtDesc(
                any(UUID.class),
                any(Pageable.class)
        );
        then(messageMapper).should().toResponse(message1);
        then(messageMapper).should().toResponse(message2);
        then(messageMapper).should(never()).toResponse(message3);
    }

    @Test
    @DisplayName("메시지 목록 조회 성공 - 커서 이후, 다음 페이지 없음")
    void findAllByChannelId_success_withCursor_noNext() {
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.parse("2026-06-04T01:00:00Z");
        Instant createdAt = Instant.parse("2026-06-04T00:59:00Z");

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .content("message")
                .createdAt(createdAt)
                .build();

        MessageResponse response = new MessageResponse(
                message.getId(),
                createdAt,
                createdAt,
                "message",
                channelId,
                null,
                List.of()
        );

        Pageable pageable = Pageable.ofSize(2);

        given(messageRepository.findAllByChannel_IdAndCreatedAtLessThanOrderByCreatedAtDesc(
                any(UUID.class),
                any(Instant.class),
                any(Pageable.class)
        )).willReturn(List.of(message));

        given(messageMapper.toResponse(message)).willReturn(response);

        PageResponse<MessageResponse> result =
                messageService.findAllByChannelId(channelId, cursor, pageable);

        assertThat(result.content()).containsExactly(response);
        assertThat(result.nextCursor()).isNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.hasNext()).isFalse();

        then(messageRepository).should().findAllByChannel_IdAndCreatedAtLessThanOrderByCreatedAtDesc(
                any(UUID.class),
                any(Instant.class),
                any(Pageable.class)
        );
        then(messageMapper).should().toResponse(message);
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() {
        UUID messageId = UUID.randomUUID();

        MessageUpdateRequest request = new MessageUpdateRequest("updated");

        Message message = Message.builder()
                .id(messageId)
                .content("old")
                .build();

        MessageResponse expectedResponse = new MessageResponse(
                messageId,
                null,
                null,
                "updated",
                null,
                null,
                List.of()
        );

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toResponse(message)).willReturn(expectedResponse);

        MessageResponse result = messageService.update(messageId, request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(message.getContent()).isEqualTo("updated");

        then(messageRepository).should().findById(messageId);
        then(messageMapper).should().toResponse(message);
    }

    @Test
    @DisplayName("메시지 수정 실패 - 메시지 없음")
    void update_fail_messageNotFound() {
        UUID messageId = UUID.randomUUID();

        MessageUpdateRequest request = new MessageUpdateRequest("updated");

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.update(messageId, request))
                .isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
        then(messageMapper).should(never()).toResponse(any());
    }

    @Test
    @DisplayName("메시지 삭제 성공 - 첨부파일 있음")
    void delete_success_withAttachments() {
        UUID messageId = UUID.randomUUID();
        UUID attachmentId1 = UUID.randomUUID();
        UUID attachmentId2 = UUID.randomUUID();

        BinaryContent attachment1 = BinaryContent.builder()
                .id(attachmentId1)
                .fileName("a.txt")
                .size(1L)
                .contentType("text/plain")
                .build();

        BinaryContent attachment2 = BinaryContent.builder()
                .id(attachmentId2)
                .fileName("b.txt")
                .size(1L)
                .contentType("text/plain")
                .build();

        Message message = Message.builder()
                .id(messageId)
                .content("message")
                .attachments(new ArrayList<>(List.of(attachment1, attachment2)))
                .build();

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        messageService.delete(messageId);

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should().delete(message);
        then(binaryContentService).should().delete(attachmentId1);
        then(binaryContentService).should().delete(attachmentId2);
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 메시지 없음")
    void delete_fail_messageNotFound() {
        UUID messageId = UUID.randomUUID();

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.delete(messageId))
                .isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).delete(any());
        then(binaryContentService).should(never()).delete(any());
    }
}