package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>(); // 선언과 동시에 초기화로 변경 (의존성 추가된 생성자에서 코드 줄임)
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(ChannelService channelService, UserService userService) {
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
    public Message update(Message message) {
        if (!data.containsKey(message.getId())) {
            return null;
        }
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

}
