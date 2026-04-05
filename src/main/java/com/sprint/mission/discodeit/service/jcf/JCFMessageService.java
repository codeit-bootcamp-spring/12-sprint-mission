package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message save(Message message) {
        if (userService.findById(message.getUserId()) == null) {
            System.out.println("존재하지 않는 사용자입니다.");
            return null;
        }
        if (channelService.findById(message.getChannelId()) == null) {
            System.out.println("존재하지 않는 채널입니다.");
            return null;
        }

        data.add(message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        for (Message message : data) {
            if (message.getId().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return data;
    }

    @Override
    public void update(UUID messageId, String content) {
        for (Message message : data) {
            if (message.getId().equals(messageId)) {
                message.update(content);
                break;
            }
        }
    }

    @Override
    public void delete(UUID messageId) {
        data.removeIf(message -> message.getId().equals(messageId));
    }
}