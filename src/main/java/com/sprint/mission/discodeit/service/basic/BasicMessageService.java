package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public MessageDto create(MessageCreateRequest request,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = request.channelId();
    UUID authorId = request.authorId();

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " not found");
    }
    if (!userRepository.existsById(authorId)) {
      throw new NoSuchElementException("Author with id " + authorId + " not found");
    }

    List<UUID> attachmentIds = binaryContentCreateRequests.stream()
        .map(attachmentRequest -> {
          BinaryContent binaryContent = new BinaryContent(
              attachmentRequest.fileName(),
              (long) attachmentRequest.bytes().length,
              attachmentRequest.contentType(),
              attachmentRequest.bytes());
          return binaryContentRepository.save(binaryContent).getId();
        })
        .toList();

    Message message = new Message(
        request.content(), channelId, authorId, attachmentIds);
    return toDto(messageRepository.save(message));
  }

  @Override
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException(
            "Message with id " + messageId + " not found"));
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException(
            "Message with id " + messageId + " not found"));
    message.update(request.newContent());
    return toDto(messageRepository.save(message));
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException(
            "Message with id " + messageId + " not found"));
    message.getAttachmentIds()
        .forEach(binaryContentRepository::deleteById);
    messageRepository.deleteById(messageId);
  }

  @Override
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId)
        .stream()
        .map(this::toDto)
        .toList();
  }

  private MessageDto toDto(Message message) {
    return new MessageDto(
        message.getId(),           // UUID id
        message.getCreatedAt(),    // Instant createdAt
        message.getUpdatedAt(),    // Instant updatedAt
        message.getContent(),      // String content
        message.getChannelId(),    // UUID channelId
        message.getAuthorId(),     // UUID authorId
        message.getAttachmentIds() // List<UUID> attachmentIds
    );
  }
}