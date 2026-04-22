package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        Message message = new Message(request.getContent(), request.getChannelId(), request.getAuthorId());
        messageRepository.save(message);

        // Save Attachment files ( Optional )
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            request.getAttachments().forEach(attachment -> {
                BinaryContent binaryContent = new BinaryContent(
                        UUID.randomUUID().toString(),
                        attachment.getBytes(),
                        attachment.getFileName(),
                        attachment.getContentType(),
                        Instant.now(),
                        null,
                        message.getId().toString()
                );
                binaryContentRepository.save(binaryContent);
            });
        }

        return message;
    }


    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message를 찾을 수 없습니다: " + messageId));
        message.update(request.getNewContent());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message를 찾을 수 없습니다: " + messageId));

        // Delete Attached File (BinaryContent)
        binaryContentRepository.findAll().stream()
                .filter(bc -> messageId.toString().equals(bc.getMessageId()))
                .forEach(bc -> binaryContentRepository.deleteById(UUID.fromString(bc.getId())));

        messageRepository.deleteById(messageId);
    }

    @Override
    public Message create(String s, UUID id, UUID id1) {
        return null;
    }
}



