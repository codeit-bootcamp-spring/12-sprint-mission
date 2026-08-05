package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> attachmentRequests) {
    if (messageCreateRequest == null) {
      throw new IllegalArgumentException("message is null.");
    }

    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() ->
            ChannelNotFoundException.withChannelId(messageCreateRequest.channelId())
        );

    User author = userRepository.findDetailById(messageCreateRequest.authorId())
        .orElseThrow(() ->
            UserNotFoundException.withUserId(messageCreateRequest.authorId())
        );

    List<BinaryContent> attachments = createAttachments(attachmentRequests);

    Message message = messageMapper.toEntity(messageCreateRequest, channel, author, attachments);
    MessageDto messageDto = messageMapper.toDto(messageRepository.save(message));

    log.info("메시지 생성 완료: messageId={}, channelId={}, authorId={}, attachmentCount={}",
        messageDto.id(),
        messageDto.channelId(),
        messageCreateRequest.authorId(),
        messageDto.attachments().size()
    );

    return messageDto;
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto findDetailById(UUID messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("id is null.");
    }

    Message message = messageRepository.findDetailById(messageId)
        .orElseThrow(() -> MessageNotFoundException.withMessageId(messageId));
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId is null.");
    }

//    (:cursor is null or m.createdAt < :cursor)로 하나의 쿼리는 에러 발생
    Slice<UUID> messageIdSlice = cursor == null
        ? messageRepository.findIdsByChannelId(channelId, pageable)
        : messageRepository.findIdsByChannelIdAndCreatedAtLessThan(channelId, cursor, pageable);

    Map<UUID, Message> messageMap = messageRepository
        .findAllDetailByIdIn(messageIdSlice.getContent()).stream()
        .collect(Collectors.toMap(Message::getId, message -> message));

    List<MessageDto> content = messageIdSlice.getContent().stream()
        .map(messageId -> messageMapper.toDto(messageMap.get(messageId)))
        .toList();

    Instant nextCursor = null;
    if (messageIdSlice.hasNext() && !content.isEmpty()) {
      nextCursor = content.get(content.size() - 1).createdAt();
    }

    return pageResponseMapper.fromSlice(content, nextCursor, messageIdSlice);
  }

  @Override
  @Transactional
  @PreAuthorize("@basicMessageService.isAuthor(#messageId, principal.userDto.id)")
  public MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest,
      List<BinaryContentCreateRequest> attachmentRequests) {
    if (messageId == null) {
      throw new IllegalArgumentException("messageId is null.");
    }
    if (messageUpdateRequest == null) {
      throw new IllegalArgumentException("messageRequest is null.");
    }

    Message message = messageRepository.findDetailById(messageId)
        .orElseThrow(() -> MessageNotFoundException.withMessageId(messageId));

    message.setContent(messageUpdateRequest.newContent());

    List<UUID> oldAttachmentIds = List.of();

    if (!attachmentRequests.isEmpty()) {
      oldAttachmentIds = message.getAttachments().stream()
          .map(BinaryContent::getId)
          .toList();

      List<BinaryContent> attachments = createAttachments(attachmentRequests);
      message.updateAttachments(attachments);
    }

    Message updatedMessage = messageRepository.save(message);
    MessageDto messageDto = messageMapper.toDto(updatedMessage);

    if (!oldAttachmentIds.isEmpty()) {
      List<UUID> deleteOldAttachmentIds = oldAttachmentIds;
      deleteBinaryContentsAfterCommit(deleteOldAttachmentIds);
    }

    log.info("메시지 업데이트 완료: messageId={}, attachmentCount={}",
        messageDto.id(),
        messageDto.attachments().size()
    );

    return messageDto;
  }

  @Override
  @Transactional
  @PreAuthorize("@basicMessageService.isAuthor(#messageId, principal.userDto.id)")
  public void delete(UUID messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("messageId is null.");
    }

    Message message = messageRepository.findDetailById(messageId)
        .orElseThrow(() -> MessageNotFoundException.withMessageId(messageId));

    List<UUID> attachmentIds = message.getAttachments().stream()
        .map(BinaryContent::getId)
        .toList();

    messageRepository.delete(message);

    deleteBinaryContentsAfterCommit(attachmentIds);

    log.info("메시지 삭제 완료: messageId={}", messageId);
  }

  private List<BinaryContent> createAttachments(
      List<BinaryContentCreateRequest> attachmentRequests) {
    if (attachmentRequests.isEmpty()) {
      return List.of();
    }

    List<BinaryContent> attachments = new ArrayList<>();

    for (BinaryContentCreateRequest attachmentRequest : attachmentRequests) {
      if (attachmentRequest == null) {
        throw new IllegalArgumentException("attachmentRequest is null.");
      }

      BinaryContent binaryContent = binaryContentRepository.save(
          binaryContentMapper.toEntity(attachmentRequest));
      binaryContentStorage.put(
          binaryContent.getId(),
          attachmentRequest.bytes(),
          binaryContent.getContentType()
      );
      log.info(
          "메시지 첨부파일 업로드 완료: binaryContentId={}, contentType={}, size={}",
          binaryContent.getId(),
          binaryContent.getContentType(),
          binaryContent.getSize()
      );
      attachments.add(binaryContent);
    }

    return attachments;
  }

  private void deleteBinaryContentsAfterCommit(List<UUID> binaryContentIds) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      binaryContentIds.forEach(binaryContentStorage::delete);
      return;
    }

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            binaryContentIds.forEach(binaryContentStorage::delete);
          }
        }
    );
  }

  public boolean isAuthor(UUID messageId, UUID userId) {
    if (messageId == null || userId == null) {
      return false;
    }

    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor() != null
            && message.getAuthor().getId().equals(userId))
        .orElse(false);
  }
}
