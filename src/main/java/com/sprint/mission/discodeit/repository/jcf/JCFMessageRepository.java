package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    // 해당 채널의 메세지만 가져오기
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();

        for (Message message : data.values()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void update(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}