package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.file.FileProcessingException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private static final int MESSAGE_PAGE_SIZE = 50;

  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public MessageResponse create(MessageCreateRequest request, List<MultipartFile> attachments) {
    User author = getUserOrThrow(request.authorId());
    Channel channel = getChannelOrThrow(request.channelId());

    List<BinaryContent> savedBinaryContents = saveAttachments(attachments);

    Message message = new Message(
        request.content(),
        channel,
        author,
        savedBinaryContents
    );

    Message savedMessage = messageRepository.save(message);

    log.info("메시지 생성 완료. messageId={}, channelId={}, authorId={}, attachmentCount={}",
        savedMessage.getId(),
        channel.getId(),
        author.getId(),
        savedBinaryContents.size()
    );
    return MessageResponse.from(savedMessage);
  }

  @Override
  public PageResponse<MessageResponse> findAllByChannelId(UUID channelId, int page) {
    Channel channel = getChannelOrThrow(channelId);

    Pageable pageable = PageRequest.of(page, MESSAGE_PAGE_SIZE);

    Slice<MessageResponse> messageResponses = messageRepository
        .findAllByChannelOrderByCreatedAtDesc(channel, pageable)
        .map(MessageResponse::from);

    return PageResponse.from(messageResponses);
  }

  @Override
  public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
    Message message = getMessageOrThrow(messageId);
    validateUserExists(message.getAuthor().getId());
    validateChannelExists(message.getChannel().getId());

    message.changeContent(request.newContent());
    Message savedMessage = messageRepository.save(message);

    log.info("메시지 수정 완료. messageId={}, channelId={}, authorId={}",
        savedMessage.getId(),
        savedMessage.getChannel().getId(),
        savedMessage.getAuthor().getId()
    );
    return MessageResponse.from(message);
  }

  @Override
  public void delete(UUID id) {
    Message message = getMessageOrThrow(id);
    List<BinaryContent> attachments = message.getAttachments();

    if (!attachments.isEmpty()) {
      binaryContentRepository.deleteAll(attachments);
    }
    messageRepository.deleteById(id);

    log.info("메시지 삭제 완료. messageId={}, attachmentCount={}",
        id,
        attachments.size()
    );
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 조회 실패 - 사용자를 찾을 수 없음. userId={}", userId);
          return new UserNotFoundException(userId);
        });
  }

  private Channel getChannelOrThrow(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널 조회 실패 - 채널을 찾을 수 없음. channelId={}", channelId);
          return new ChannelNotFoundException(channelId);
        });
  }


  private Message getMessageOrThrow(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> {
              log.warn("메시지 조회 실패 - 메시지를 찾을 수 없음. messageId={}", messageId);
              return new MessageNotFoundException(messageId);
            }
        );
  }

  private void validateUserExists(UUID userId) {
    userRepository.findById(userId).orElseThrow(() -> {
      log.warn("사용자 검증 실패 - 사용자를 찾을 수 없음. userId={}", userId);
      return new UserNotFoundException(userId);
    });
  }

  private void validateChannelExists(UUID channelId) {
    channelRepository.findById(channelId).orElseThrow(() -> {
      log.warn("채널 검증 실패 - 채널을 찾을 수 없음. channelId={}", channelId);
      return new ChannelNotFoundException(channelId);
    });
  }

  private List<BinaryContent> saveAttachments(List<MultipartFile> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return List.of();
    }

    List<BinaryContent> savedBinaryContents = new ArrayList<>();

    for (MultipartFile file : attachments) {
      try {
        BinaryContent binaryContent = new BinaryContent(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize()
        );

        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(
            savedBinaryContent.getId(),
            file.getBytes()
        );

        savedBinaryContents.add(savedBinaryContent);
      } catch (IOException e) {
        log.error("첨부파일 저장 실패. fileName={}, contentType={}, size={}",
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            e
        );

        throw new FileProcessingException(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            e
        );
      }
    }

    return savedBinaryContents;
  }

}
