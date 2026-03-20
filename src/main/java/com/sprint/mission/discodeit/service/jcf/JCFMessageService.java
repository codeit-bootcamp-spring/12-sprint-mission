package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages;

    public JCFMessageService() {
        messages = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            return null;
        }

        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId) {
        Message message = messages.get(messageId);

        if (message != null) {
            message.update(content, channelId);
            return message;
        }

        return null;
    }

    @Override
    public Message delete(UUID messageId) {
        return messages.remove(messageId);
    }
}
