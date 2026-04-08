package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final UserService userService;

    public BasicMessageService(MessageRepository messageRepository,
                               ChannelService channelService,
                               UserService userService) {
        this.messageRepository = messageRepository;
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message create(Message message) {
        if (channelService.findById(message.getChannelId()).isEmpty()) {
            return null;
        }

        if (userService.findById(message.getUserId()).isEmpty()) {
            return null;
        }

        return messageRepository.create(message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(Message message) {
        return messageRepository.update(message);
    }

    @Override
    public void delete(UUID id) {
        messageRepository.delete(id);
    }
}