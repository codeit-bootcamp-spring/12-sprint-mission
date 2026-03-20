package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            return null;
        }

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId) {
        Message message = findById(messageId);

        if (message != null) {
            message.update(content, channelId);
        }

        return message;
    }

    @Override
    public Message delete(UUID messageId) {
        return data.remove(messageId);
    }
}
