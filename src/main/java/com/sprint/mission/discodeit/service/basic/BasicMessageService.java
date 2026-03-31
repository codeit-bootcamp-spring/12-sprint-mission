package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MessageData;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
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
        Optional<Message> message = messageRepository.findByContent(content);

        if (message.isPresent()) {
            return message.get();
        } else {
            throw new IllegalArgumentException("해당 이름을 가진 메시지 없음");
        }
    }

    @Override
    public List<Message> findAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public Message changeMessageContent(UUID id, String content) {
        Optional<Message> opMsg = messageRepository.findById(id);

        if (opMsg.isEmpty()) {
            throw new IllegalArgumentException("해당 id를 가진 메시지 없음.");
        }
        Message msg = opMsg.get();

        msg.update(msg.getAuthorId(), msg.getChannelId(), content);

        return messageRepository.save(msg);
    }

    @Override
    public Message deleteMessage(UUID id) {
        return messageRepository.delete(id);
    }
}