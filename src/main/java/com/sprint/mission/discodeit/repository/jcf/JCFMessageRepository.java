package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        if (messages.containsKey(id)) {
            return messages.get(id);
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        List<Message> findmessages = new ArrayList<>();
        for (Message message : messages.values()) {
            findmessages.add(message);
        }
        return findmessages;
//        return messages.values().stream().toList();
    }

    @Override
    public Message update(Message message) {
        if (messages.containsKey(message.getId())) {
            messages.put(message.getId(), message);
            return message;
        }
        return null;
    }

    @Override
    public Message delete(UUID id) {
        Message message = messages.get(id);
        messages.remove(id);
        return message;
    }
}
