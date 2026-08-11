package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentService binaryContentService;
    private final UserMapper userMapper;
    private final JwtRegistry jwtRegistry;

    @Override
    @Transactional
    public MessageResponse create(
            MessageCreateRequest request,
            List<BinaryContentCreateRequest> binaryContentCreateRequests
    ) {
        log.info(
                "Message create requested. channelId={}, authorId={}, attachmentCount={}",
                request.channelId(),
                request.authorId(),
                binaryContentCreateRequests.size()
        );

        Channel channel = getChannelOrThrow(request.channelId());
        User author = getUserOrThrow(request.authorId());

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(binaryContentService::createBinaryContent)
                .toList();

        Message message = messageMapper.toEntity(request, channel, author, attachments);
        Message saved = messageRepository.save(message);

        log.info(
                "Message created. messageId={}, channelId={}, authorId={}",
                saved.getId(),
                channel.getId(),
                author.getId()
        );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Message find(UUID messageId) {
        return getMessageOrThrow(messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> findAllByChannelId(
            UUID channelId,
            Instant cursor,
            Pageable pageable
    ) {
        log.debug(
                "Message findAllByChannelId requested. channelId={}, cursor={}, size={}",
                channelId,
                cursor,
                pageable.getPageSize()
        );

        int size = pageable.getPageSize();
        Pageable requestPageable = PageRequest.of(0, size + 1);

        List<Message> messages = cursor == null
                ? messageRepository.findAllByChannel_IdOrderByCreatedAtDesc(
                channelId,
                requestPageable
        )
                : messageRepository.findAllByChannel_IdAndCreatedAtLessThanOrderByCreatedAtDesc(
                channelId,
                cursor,
                requestPageable
        );

        boolean hasNext = messages.size() > size;

        List<Message> pageMessages = hasNext
                ? messages.subList(0, size)
                : messages;

        List<MessageResponse> content = pageMessages.stream()
                .map(this::toResponse)
                .toList();

        Instant nextCursor = hasNext && !pageMessages.isEmpty()
                ? pageMessages.get(pageMessages.size() - 1).getCreatedAt()
                : null;

        log.debug(
                "Message findAllByChannelId completed. channelId={}, resultCount={}, hasNext={}",
                channelId,
                content.size(),
                hasNext
        );

        return new PageResponse<>(
                content,
                nextCursor,
                size,
                hasNext,
                null
        );
    }

    @PreAuthorize("@messageSecurity.isOwner(#messageId, authentication)")
    @Override
    @Transactional
    public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
        log.info("Message update requested. messageId={}", messageId);

        Message message = getMessageOrThrow(messageId);

        message.updateContent(request.newContent());

        log.info("Message updated. messageId={}", message.getId());

        return toResponse(message);
    }

    @PreAuthorize("@messageSecurity.isOwner(#messageId, authentication)")
    @Override
    @Transactional
    public void delete(UUID messageId) {
        log.warn("Message delete requested. messageId={}", messageId);

        Message message = getMessageOrThrow(messageId);
        List<UUID> attachmentIds = getAttachmentIds(message);

        message.clearAttachments();
        messageRepository.delete(message);

        for (UUID attachmentId : attachmentIds) {
            binaryContentService.delete(attachmentId);
        }

        log.info("Message deleted. messageId={}, deletedAttachmentCount={}", messageId, attachmentIds.size());
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    @Transactional
    public void deleteByChannelManager(UUID messageId) {
        deleteInternal(messageId);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
    }

    private List<UUID> getAttachmentIds(Message message) {
        return message.getAttachments().stream()
                .map(BinaryContent::getId)
                .toList();
    }

    private void deleteInternal(UUID messageId) {
        Message message = getMessageOrThrow(messageId);
        List<UUID> attachmentIds = getAttachmentIds(message);

        message.clearAttachments();
        messageRepository.delete(message);

        for (UUID attachmentId : attachmentIds) {
            binaryContentService.delete(attachmentId);
        }
    }

    private MessageResponse toResponse(Message message) {
        User author = message.getAuthor();

        UserResponse authorResponse = author == null
                ? null
                : userMapper.toResponse(
                author,
                jwtRegistry.hasActiveJwtInformationByUserId(author.getId())
        );

        return messageMapper.toResponse(message, authorResponse);
    }
}