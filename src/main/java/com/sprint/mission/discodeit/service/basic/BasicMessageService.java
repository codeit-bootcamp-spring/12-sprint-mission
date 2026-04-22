package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.CreateAttachmentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
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

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(CreateMessageRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel with id " + request.channelId() + " not found");
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("User with id " + request.authorId() + " not found" );
        }
        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId()
        );

        List<CreateAttachmentRequest> attachments = request.attachments();
        if (attachments != null) {
            for (CreateAttachmentRequest attachment : attachments) {
                BinaryContent binaryContent = new BinaryContent(
                        UUID.randomUUID(),
                        attachment.fileName(),
                        attachment.contentType(),
                        attachment.bytes()
                );
                BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
                message.addAttachment(savedBinaryContent.getId());
            }
        }
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(UpdateMessageRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.messageId() + " not found"));
        message.update(request.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.findById(attachmentId)
                    .ifPresent(binaryContent -> binaryContentRepository.deleteById(attachmentId));
        }
        messageRepository.deleteById(messageId);
    }
}

