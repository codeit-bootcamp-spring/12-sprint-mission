package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();
    Channel channel = channelRepository.findById(channelId).orElseThrow(
        () -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
    User author = userRepository.findById(authorId).orElseThrow(
        () -> new NoSuchElementException("Author with id " + authorId + " does not exist"));
    if (channel.getType() == ChannelType.PRIVATE) {
      boolean isMember = readStatusRepository.existsAllByChannelIdAndUserId(channelId, authorId);
      if (!isMember) {
        throw new IllegalArgumentException(
            "Author with id " + authorId + " is not a member of this private channel.");
      }
    }

    List<BinaryContent> attachments = Collections.emptyList();
    if (!binaryContentCreateRequests.isEmpty()) {
      attachments = binaryContentCreateRequests.stream()
          .map(attachmentRequest -> {
            String fileName = attachmentRequest.fileName();
            String contentType = attachmentRequest.contentType();
            byte[] bytes = attachmentRequest.bytes();

            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                contentType);
            BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(savedBinaryContent.getId(), bytes);
            return savedBinaryContent;
          }).toList();
    }
    String content = messageCreateRequest.content();
    Message message = Message.builder().channel(channel).author(author).content(content)
        .attachments(attachments).build();
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId).map(messageMapper::toDto).orElseThrow(
        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    if (cursor == null) {
      cursor = Instant.now();
    }
    Slice<MessageDto> messageSlice = messageRepository.findMessagesByChannelIdBeforeCursor(
        channelId,
        cursor, pageable).map(messageMapper::toDto);
    Instant nextCursor = null;
    if (!messageSlice.getContent().isEmpty()) {
      nextCursor = messageSlice.getContent().get(messageSlice.getContent().size() - 1).createdAt();
    }
    return pageResponseMapper.fromSlice(messageSlice, nextCursor);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId).orElseThrow(
        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(newContent);
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId).orElseThrow(
        () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    binaryContentRepository.deleteAll(message.getAttachments());

    messageRepository.deleteById(messageId);
  }
}
