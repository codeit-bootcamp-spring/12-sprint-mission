package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Primary
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        Message message = new Message(request.getContent(), request.getChannelId(), request.getAuthorId());
        messageRepository.save(message);

    // Saving Attachment files ( Optional )
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
            .orElseThrow(() -> new NoSuchElementException("Message를 찾을 수 없습니다: " + messageId));
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

        // Deleting 첨부파일(BinaryContent)
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


