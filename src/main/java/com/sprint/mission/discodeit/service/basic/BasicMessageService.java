package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateDTO messageCreateDTO) {
        if (!channelRepository.existsById(messageCreateDTO.getChannelId())) {
            throw new NoSuchElementException("Channel not found with id " + messageCreateDTO.getChannelId());
        }
        if (!userRepository.existsById(messageCreateDTO.getAuthorId())) {
            throw new NoSuchElementException("Author not found with id " + messageCreateDTO.getAuthorId());
        }

        List<UUID> attachmentIds = new ArrayList<>();
        if (messageCreateDTO.getAttachmentTypes() != null) {
            attachmentIds.addAll(createBinaryContents(messageCreateDTO.getAttachmentTypes(), messageCreateDTO.getAttachments()));
        }
        Message message = new Message(messageCreateDTO.getContent(),
                messageCreateDTO.getChannelId(),
                messageCreateDTO.getAuthorId(),
                attachmentIds);
        return messageRepository.save(message);
    }

    private List<UUID> createBinaryContents(List<String> types, List<byte[]> attachments) {
        List<UUID> attachmentIds = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            BinaryContent binaryContent = new BinaryContent(types.get(i), attachments.get(i));
            attachmentIds.add(binaryContent.getId());
            binaryContentRepository.save(binaryContent);
        }
        return attachmentIds;
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message update(MessageUpdateDTO messageUpdateDTO) {
        Message message = messageRepository.findById(messageUpdateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageUpdateDTO.getId() + " not found"));

        List<UUID> attachmentIds = new ArrayList<>();
        if (messageUpdateDTO.getAttachmentTypes() != null) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }
            attachmentIds.addAll(createBinaryContents(messageUpdateDTO.getAttachmentTypes(), messageUpdateDTO.getAttachments()));
        }
        message.update(messageUpdateDTO.getContent(), attachmentIds);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }
        messageRepository.deleteById(messageId);
    }
}
