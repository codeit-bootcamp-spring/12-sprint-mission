package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MessageData;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(MessageData messageData) {
        Message message = new Message(messageData.authorId(), messageData.channelId(), messageData.content());
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message findMessageByContent(String content) {
        return messageRepository.findMessageByContent(content);
    }

    @Override
    public List<Message> findAllMessage() {
        return messageRepository.findAllMessage();
    }

    @Override
    public Message changeMessageContent(UUID id, String content) {
        return messageRepository.changeMessageContent(id, content);
    }

    @Override
    public Message deleteMessage(UUID id) {
        return messageRepository.deleteMessage(id);
    }
}