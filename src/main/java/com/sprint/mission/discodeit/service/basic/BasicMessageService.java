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
import com.sprint.mission.discodeit.exception.channel.ChannelAccessDeniedException;
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
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    log.debug("Message 생성 시작: messageCreateRequest={}, binaryContentCreateRequestsSize={}",
        messageCreateRequest, binaryContentCreateRequests.size());
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();
    Channel channel = channelRepository.findById(channelId).orElseThrow(
        () -> {
          log.warn("Message 생성 실패(채널 없음): channelId={}", channelId);
          return ChannelNotFoundException.withId(channelId);
        });
    User author = userRepository.findById(authorId).orElseThrow(
        () -> {
          log.warn("Message 생성 실패(작성자 없음): authorId={}", authorId);
          return UserNotFoundException.withId(authorId);
        });
    if (channel.getType() == ChannelType.PRIVATE) {
      boolean isMember = readStatusRepository.existsAllByChannelIdAndUserId(channelId, authorId);
      if (!isMember) {
        log.warn("Message 생성 실패(권한 없음): channelId={}, authorId={}", channelId, authorId);
        throw ChannelAccessDeniedException.withChannelIdAndUserId(channelId, authorId);
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
    log.info("Message 생성 성공: channelId={}, authorId={}, contentLength={}, attachmentsSize={}",
        channelId, authorId, content.length(), attachments.size());
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  public MessageDto find(UUID messageId) {
    log.debug("Message 조회 시작: messageId={}", messageId);
    MessageDto messageDto = messageRepository.findById(messageId).map(messageMapper::toDto)
        .orElseThrow(
            () -> {
              log.warn("Message 조회 실패(Message 없음): messageId={}", messageId);
              return MessageNotFoundException.withId(messageId);
            });
    log.info(
        "Message 조회 성공: messageId={}, channelId={}, authorId={}, contentLength={}, attachmentsSize={}",
        messageId, messageDto.channelId(), messageDto.author(), messageDto.content().length(),
        messageDto.attachments().size());
    return messageDto;
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    log.debug("채널 메시지 조회 시작: channelId={}, cursor={}, pageSize={}", channelId, cursor,
        pageable.getPageSize());
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
    log.info("채널 메시지 조회 성공: channelId={}, messageCount={}, nextCursor={}", channelId,
        messageSlice.getNumberOfElements(), nextCursor);
    return pageResponseMapper.fromSlice(messageSlice, nextCursor);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN') or @basicMessageService.isOwner(authentication.principal.userDto.id, messageId)")
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    log.debug("Message 업데이트 시작: messageId={}, request={}", messageId, request);
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId).orElseThrow(
        () -> {
          log.warn("Message 업데이트 실패(Message 없음): messageId={}", messageId);
          return MessageNotFoundException.withId(messageId);
        });
    message.update(newContent);
    log.info("Message 업데이트 성공: messageId={}, newContentLength={}", messageId, newContent.length());
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN') or @basicMessageService.isOwner(authentication.principal.userDto.id, messageId)")
  @Transactional
  public void delete(UUID messageId) {
    log.debug("Message 삭제 시작: messageId={}", messageId);
    Message message = messageRepository.findById(messageId).orElseThrow(
        () -> {
          log.warn("Message 삭제 실패(Message 없음): messageId={}", messageId);
          return MessageNotFoundException.withId(messageId);
        });

    binaryContentRepository.deleteAll(message.getAttachments());

    messageRepository.deleteById(messageId);
    log.info("Message 삭제 성공: messageId={}", messageId);
  }

  public boolean isOwner(UUID userId, UUID messageId) {
    return messageRepository.findById(messageId)
        .map(m -> m.getAuthor().getId().equals(userId))
        .orElse(false);
  }
}
