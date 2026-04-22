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
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messages.get(id));
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
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> findmessages = new ArrayList<>();
        for (Message message : messages.values()) {
            if (message.getChannelId().equals(channelId)) {
                findmessages.add(message);
            }
        }
        return findmessages;
    }


    @Override
    public void delete(UUID id) {
        messages.remove(id);
    }
}
