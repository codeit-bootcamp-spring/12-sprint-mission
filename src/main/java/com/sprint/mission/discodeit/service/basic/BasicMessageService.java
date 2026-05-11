package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }

        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found with id " + request.authorId());
        }

        if (request.attachmentIds() != null) {
            request.attachmentIds().forEach(attachmentId -> {
                if (!binaryContentRepository.existsById(attachmentId)) {
                    throw new NoSuchElementException("Attachment not found with id " + attachmentId);
                }
            });
        }

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                request.attachmentIds()
        );

        messageRepository.save(message);

        // 메세지 시간 업데이트?

        return MessageDto.from(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found (message -> find)."));
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found (message -> findAllByChannelId).)"));

        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(MessageDto::from)
                .toList();
    }

    @Override
    public MessageDto update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.id() + " not found"));

        message.update(request.content());
        messageRepository.save(message);

        return MessageDto.from(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        List<UUID> attachmentIds = message.getAttachmentIds();

        messageRepository.deleteById(messageId);

        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (UUID attachmentId : attachmentIds) {
                binaryContentRepository.delete(attachmentId);
            }
        }
    }

    public int totalMessageNumber() {
        return messageRepository.findAll().size();
    }
}
