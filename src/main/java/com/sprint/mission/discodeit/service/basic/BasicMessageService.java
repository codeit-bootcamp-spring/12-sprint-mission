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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public MessageDto create(MessageCreateRequest req,
      List<BinaryContentCreateRequest> attachmentRequests) {
    Channel channel = channelRepository.findById(req.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(req.channelId()));
    User author = userRepository.findById(req.authorId())
        .orElseThrow(() -> new UserNotFoundException(req.authorId()));

    // 첨부파일: DB에 메타 저장 후 storage에 파일 저장
    List<BinaryContent> attachments = attachmentRequests.stream()
        .map(a -> {
          BinaryContent bc = binaryContentRepository.save(
              new BinaryContent(a.fileName(), (long) a.bytes().length, a.contentType()));
          binaryContentStorage.put(bc.getId(), a.bytes());
          return bc;
        })
        .toList();

    Message message = new Message(req.content(), channel, author, attachments);
    Message saved = messageRepository.save(message);
    log.info("Message created: id={}, channelId={}", saved.getId(), req.channelId());
    return messageMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(() -> new MessageNotFoundException(messageId));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
    log.debug("Fetching messages for channelId={}, cursor={}, size={}", channelId, cursor, size);
    // 커서 기반 페이지네이션: cursor 이전 메시지를 createdAt DESC로 조회
    // JOIN FETCH로 author, attachments를 한 번의 쿼리로 로딩 (N+1 방지)
    PageRequest pageRequest = PageRequest.of(0, size);
    Slice<Message> slice = cursor == null
        ? messageRepository.findFirstPageByChannelId(channelId, pageRequest)
        : messageRepository.findNextPageByChannelId(channelId, cursor, pageRequest);

    // 다음 페이지 커서: 현재 결과의 마지막 메시지 createdAt
    Instant nextCursor = slice.hasNext()
        ? slice.getContent().get(slice.getContent().size() - 1).getCreatedAt()
        : null;

    Slice<MessageDto> dtoSlice = slice.map(messageMapper::toDto);
    return pageResponseMapper.fromSlice(dtoSlice, nextCursor);
  }

  // 메시지 수정/삭제는 작성자만 가능
  @Override
  @PreAuthorize("@messageAuthorizer.isAuthor(#messageId, principal.userId)")
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId));
    message.update(request.newContent());
    log.info("Message updated: id={}", messageId);
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  @PreAuthorize("@messageAuthorizer.isAuthor(#messageId, principal.userId)")
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId));
    messageRepository.delete(message);
    log.info("Message deleted: id={}", messageId);
  }
}
