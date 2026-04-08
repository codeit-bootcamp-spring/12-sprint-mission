package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap = new HashMap<>();

    @Override
    public Message save(Message message) {
       messageMap.put(message.getMessageId(), message);
       return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public List<Message> findAll() {
       return new ArrayList<>(messageMap.values());
    }

    @Override
    public void deleteById(UUID id) {
        messageMap.remove(id);
    }
}
