package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(String content, String sender, String receiver) {
        Message message = new Message(content, sender, receiver);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message read(UUID id, Message message) {
        return (Message) messageRepository;
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(messageRepository.findAll());
    }

    @Override
    public void delete(String message) {

    }
}
