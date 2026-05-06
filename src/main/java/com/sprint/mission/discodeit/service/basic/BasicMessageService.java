package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.message.Message;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
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
    public MessageResponse create(MessageCreateRequest dto, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        if (!channelRepository.existsById(dto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + dto.channelId());
        }

        if (!userRepository.existsById(dto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + dto.authorId());
        }

        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    BinaryContent binaryContent = new BinaryContent(
                            attachmentRequest.data(),
                            attachmentRequest.filename(),
                            attachmentRequest.mimeType());
                    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
                    return createdBinaryContent.getId();
                })
                .toList();

        Message message = new Message(
                dto.content(),
                dto.channelId(),
                dto.authorId(),
                attachmentIds
        );
        messageRepository.save(message);

        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }

    @Override
    public Message find(UUID messageId) {
        return getMessageOrThrow(messageId);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getCreatedAt(),
                        message.getUpdatedAt(),
                        message.getContent(),
                        message.getChannelId(),
                        message.getAuthorId(),
                        message.getAttachmentIds()
                ))
                .toList();
    }

    @Override
    public MessageResponse update(UUID messageId, MessageUpdateRequest dto) {
        Message message = getMessageOrThrow(messageId);

        message.update(dto.newContent());
        messageRepository.save(message);

        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }

    @Override
    public void delete(UUID messageId) {
        Message message = getMessageOrThrow(messageId);

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }
        messageRepository.deleteById(messageId);
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }
}
