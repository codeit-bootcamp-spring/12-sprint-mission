package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BasicMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    public BasicMessageService(UserService userService, ChannelService channelService, MessageRepository messageRepository) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message save(Message message) {
        userService.findById(message.getUserId());
        channelService.findById(message.getChannelId());
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        Message message = messageRepository.findById(messageId);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 메시지: " + messageId);
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public void update(UUID messageId, String content) {
        Message message = findById(messageId);
        message.update(content);
        messageRepository.update(message);
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.delete(messageId);
    }
}