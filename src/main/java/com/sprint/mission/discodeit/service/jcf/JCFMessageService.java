package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;

    private final List<Message> data;

    public JCFMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
        data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
        if (userService.findById(message.getMemberId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 유저 ID 입니다.");
        }
        if (channelService.findById(message.getChannelId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 채널 ID 입니다.");
        }
        data.add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        for(Message message : data) {
            if(message.getId().equals(id)) return Optional.of(message);
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }

    @Override
    public Message update(UUID id, Message message, List<UUID> attachmentIds) {
        Optional<Message> found = findById(id);
        if (found.isPresent()) {
            found.get().update(message.getContent(), attachmentIds);
            return found.orElse(null);
        }
        return null;
    }

    @Override
    public boolean delete(UUID id) {
        for(Message message : data) {
            if(message.getId().equals(id)) {
                data.remove(message);
                return true;
            }
        }
        return false;
    }
}
