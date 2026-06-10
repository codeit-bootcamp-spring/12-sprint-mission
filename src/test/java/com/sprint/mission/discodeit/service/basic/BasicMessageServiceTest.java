package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private MessageMapper messageMapper;

  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

  private UUID channelId;
  private UUID authorId;
  private UUID messageId;
  private UUID binaryContentId;
  private String content;
  private Channel channel;
  private User author;
  private UserDto authorDto;
  private Message message;
  private MessageDto messageDto;
  private BinaryContent binaryContent;
  private BinaryContentDto binaryContentDto;
  private Instant now;

  @BeforeEach
  public void setUp() {
    channelId = UuidCreator.getTimeOrderedEpoch();
    authorId = UuidCreator.getTimeOrderedEpoch();
    messageId = UuidCreator.getTimeOrderedEpoch();
    content = "test message";
    now = Instant.now();

    channel = new Channel(ChannelType.PUBLIC, "test channel", "test description");
    ReflectionTestUtils.setField(channel, "id", channelId);

    author = new User("testauthor", "test@test.com", "password1234!", null);
    ReflectionTestUtils.setField(author, "id", authorId);
    authorDto = new UserDto(authorId, "testauthor", "test@test.com", null, true);

    binaryContent = new BinaryContent("testfile.png", 1000L, "image/png");
    ReflectionTestUtils.setField(binaryContent, "id", binaryContentId);
    binaryContentDto = new BinaryContentDto(binaryContentId, "testfile.png", 1000L, "image/png");

    message = Message.builder()
        .content(content)
        .channel(channel)
        .author(author)
        .attachments(List.of(binaryContent))
        .build();
    ReflectionTestUtils.setField(message, "id", messageId);
    messageDto = new MessageDto(messageId, Instant.now(), now, content, channelId, authorDto,
        List.of(binaryContentDto));
  }

  // ── create ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 생성 테스트(성공)")
  public void create() {
    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest("testfile.png",
        "image/png", new byte[1000]);
    List<BinaryContentCreateRequest> attachmentsRequest = List.of(attachmentRequest);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(binaryContentRepository.save(any())).willReturn(binaryContent);
    given(binaryContentStorage.put(any(), any())).willReturn(binaryContentId);
    given(messageRepository.save(any())).willReturn(message);
    given(messageMapper.toDto(message)).willReturn(messageDto);

    MessageDto result = messageService.create(request, attachmentsRequest);

    assertThat(result).isEqualTo(messageDto);
    verify(binaryContentRepository).save(any());
    verify(binaryContentStorage).put(any(), any());
    verify(messageRepository).save(any());
    verify(messageMapper).toDto(message);
  }

  @Test
  @DisplayName("메시지 생성 테스트(실패 - 채널 없음)")
  public void create_channelNotFound() {
    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    List<BinaryContentCreateRequest> attachmentsRequest = List.of();
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());
    assertThatThrownBy(() -> messageService.create(request, attachmentsRequest))
        .isInstanceOf(ChannelNotFoundException.class);
    verify(channelRepository).findById(channelId);
    verify(messageRepository, never()).save(any());
  }

  @Test
  @DisplayName("메시지 생성 테스트(실패 - 작성자 없음)")
  public void create_userNotFound() {
    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    List<BinaryContentCreateRequest> attachmentsRequest = List.of();
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, attachmentsRequest))
        .isInstanceOf(UserNotFoundException.class);

    verify(channelRepository).findById(channelId);
    verify(messageRepository, never()).save(any());
  }

  // ── findByChannel ───────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 메시지 조회 테스트(성공)")
  public void findByChannel() {
    int pageSize = 2;
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, pageSize);

    Message message1 = Message.builder()
        .content(content + "1")
        .channel(channel)
        .author(author)
        .attachments(List.of(binaryContent))
        .build();
    Message message2 = Message.builder()
        .content(content + "2")
        .channel(channel)
        .author(author)
        .attachments(List.of(binaryContent))
        .build();

    ReflectionTestUtils.setField(message1, "id", UuidCreator.getTimeOrderedEpoch());
    ReflectionTestUtils.setField(message2, "id", UuidCreator.getTimeOrderedEpoch());

    Instant message1CreatedAt = Instant.now().minusSeconds(30);
    Instant message2CreatedAt = Instant.now().minusSeconds(20);

    ReflectionTestUtils.setField(message1, "createdAt", message1CreatedAt);
    ReflectionTestUtils.setField(message2, "createdAt", message2CreatedAt);

    MessageDto messageDto1 = new MessageDto(
        message1.getId(),
        message1CreatedAt,
        message1CreatedAt,
        content + "1",
        channelId,
        authorDto,
        List.of(binaryContentDto)
    );

    MessageDto messageDto2 = new MessageDto(
        message2.getId(),
        message2CreatedAt,
        message2CreatedAt,
        content + "2",
        channelId,
        authorDto,
        List.of(binaryContentDto)
    );

    List<MessageDto> firstPageDtos = List.of(messageDto1, messageDto2);

    SliceImpl<Message> slice = new SliceImpl<>(List.of(message1, message2), pageable, true);
    PageResponse<MessageDto> pageResponse = new PageResponse<>(
        firstPageDtos,
        message2CreatedAt,
        pageSize,
        true
    );

    given(messageRepository.findMessagesByChannelIdBeforeCursor(channelId, cursor, pageable))
        .willReturn(slice);
    given(messageMapper.toDto(message1)).willReturn(messageDto1);
    given(messageMapper.toDto(message2)).willReturn(messageDto2);
    given(pageResponseMapper.fromSlice(any(), any())).willReturn((PageResponse) pageResponse);

    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor,
        pageable);

    assertThat(result).isEqualTo(pageResponse);
  }

  // ── update ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 테스트(성공)")
  public void update() {
    String newContent = "updated message";
    MessageUpdateRequest request = new MessageUpdateRequest(newContent);
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageRepository.save(any())).willReturn(message);
    given(messageMapper.toDto(message)).willReturn(messageDto);

    MessageDto result = messageService.update(messageId, request);

    assertThat(result).isEqualTo(messageDto);
  }

  // ── delete ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 테스트(실패 - 메시지 없음)")
  public void update_notFound() {
    String newContent = "updated message";
    MessageUpdateRequest request = new MessageUpdateRequest(newContent);
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 삭제 테스트(성공)")
  public void delete() {
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    messageService.delete(messageId);

    verify(binaryContentRepository).deleteAll(List.of(binaryContent));
    verify(messageRepository).deleteById(messageId);
  }
}
