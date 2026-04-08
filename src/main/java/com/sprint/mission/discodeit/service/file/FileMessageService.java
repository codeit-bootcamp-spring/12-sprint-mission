package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    public FileMessageService(UserService userService, ChannelService channelService, MessageRepository messageRepository) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message save(Message message) {
        if (userService.findById(message.getUserId()) == null) {
            throw new NoSuchElementException("존재하지 않는 사용자");
        }
        if (channelService.findById(message.getChannelId()) == null) {
            throw new NoSuchElementException("존재하지 않는 채널");
        }

        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public void update(UUID messageId, String content) {
        Message message = messageRepository.findById(messageId);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 메시지");
        }
        message.update(content);
        messageRepository.update(message);
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.delete(messageId);
    }
}