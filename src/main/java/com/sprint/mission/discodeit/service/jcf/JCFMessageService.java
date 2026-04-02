package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        if(userId == null){
            throw new RuntimeException("유저 없음");
        }
        if(channelId == null){
            throw new RuntimeException("채널 없음");
        }
        Message message = new Message(userId,channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, String content) {
        Message message = data.get(id);
        if(message == null){
            throw new RuntimeException("메시지 없음");
        }
        message.update(content);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}

