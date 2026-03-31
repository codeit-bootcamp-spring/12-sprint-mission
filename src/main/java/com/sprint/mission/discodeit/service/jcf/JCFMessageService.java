package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final MessageRepository messageRepository;

    // 의존성 주입
    public JCFMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessage(UUID channelId, UUID authorId, String content) {
        Message message = new Message(channelId, authorId, content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = messageRepository.findById(id);
        if (message != null) {
            message.update(content);
            messageRepository.update(message);
        }
        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        messageRepository.delete(id);
    }
}