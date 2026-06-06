package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
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
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(
      MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests
  ) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    log.debug("메세지 생성 요청: channelId={}, authorId={}, attachmentCount={}",
        channelId, authorId, binaryContentCreateRequests.size());

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("메세지 생성 실패 - 채널을 찾을 수 없음: channelId={}", channelId);
          return new NoSuchElementException("Channel with id " + channelId + " does not exist");
        });

    User author = userRepository.findById(authorId)
        .orElseThrow(() -> {
          log.warn("메세지 생성 실패 - 작성자를 찾을 수 없음: authorId{}", authorId);
          return new NoSuchElementException("Author with id " + authorId + " does not exist");
        });

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes = attachmentRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);

          log.info("메세지 첨부 파일 업로드 완료: fileId={}, fileName={}, size={}",
              binaryContent.getId(), fileName, bytes.length);

          return binaryContent;
        })
        .toList();

    String content = messageCreateRequest.content();
    Message message = new Message(
        content,
        channel,
        author,
        attachments
    );

    messageRepository.save(message);

    log.info("메세지 생성 완료: messageId={}, channelId={}, authorId={}, attachmentCount={}",
        message.getId(), channelId, authorId, attachments.size());

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(() ->
            new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createdAt,
      Pageable pageable) {
    Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
            Optional.ofNullable(createdAt).orElse(Instant.now()),
            pageable)
        .map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1)
          .createdAt();
    }
    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();

    log.debug("메세지 수정 요청: messageId={}, contentLength={}",
        messageId, newContent.length());

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("메세지 수정 실패 - 메세지를 찾을 수 없음: messageId={}", messageId);
          return new NoSuchElementException("Message with id " + messageId + " not found");
        });
    message.update(newContent);

    log.info("메세지 수정 완료: messageId={}", messageId);

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    log.debug("메세지 삭제 요청: messageId={}", messageId);
    if (!messageRepository.existsById(messageId)) {
      log.warn("메세지 삭제 실패 - 메세지를 찾을 수 없음: messageId={}", messageId);
      throw new NoSuchElementException("Message with id " + messageId + " not found");
    }

    messageRepository.deleteById(messageId);

    log.info("메세지 삭제 완료: messageId={}", messageId);
  }
}