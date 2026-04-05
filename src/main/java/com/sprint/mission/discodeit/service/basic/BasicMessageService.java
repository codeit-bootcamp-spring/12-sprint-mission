package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    // 필요한 Repository 인터페이스를 필드로 선언
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자를 통해 초기화
    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message sendMessage(UUID channelId, UUID authorId, String content) {
        // 비즈니스 로직?
        if (channelService.getChannel(channelId) == null) {
            System.out.println("존재하지 않는 채팅방입니다.");
            return null;
        }
        if (userService.getUser(authorId) == null) {
            System.out.println("존재하지 않는 유저입니다.");
            return null;
        }

        // 비즈니스 로직
        Message message = new Message(channelId, authorId, content);

        // 저장 로직
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
            // 비즈니스 로직
            message.update(content);

            // 저장 로직
            messageRepository.update(message);
        }
        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        messageRepository.delete(id);
    }
}