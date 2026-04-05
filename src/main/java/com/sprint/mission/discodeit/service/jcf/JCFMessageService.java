package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

// 서비스 간 의존성 주입 (스프린트 2 - 1차 미션 심화)
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;


public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageRepo;
    // userService, ChannelService 추가
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        messageRepo = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message send(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메시지가 null입니다.");
        }

        if (message.getContent() == null || message.getContent().isBlank()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }

        User user = message.getSender();
        if (user == null || userService.getUser(user.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Channel channel = message.getChannel();
        if (channel == null || channelService.getChannel(channel.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        messageRepo.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return messageRepo.get(id);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();

        for (Message message : messageRepo.values()) {
            if (message.getChannel().getId().equals(channelId)) {
                result.add(message);
            }
        }

        return result;
    }

    @Override
    public Message edit(UUID id, String content) {
        Message message = messageRepo.get(id);

        if (message != null) {
            message.updateContent(content);
        }

        return message;
    }

    @Override
    public void remove(UUID id) {
        messageRepo.remove(id);
    }
}
