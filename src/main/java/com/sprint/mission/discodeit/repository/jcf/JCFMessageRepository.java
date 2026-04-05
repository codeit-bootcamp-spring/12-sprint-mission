package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data;

    public JCFMessageRepository() {
        data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
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
        return new ArrayList<>(data);
    }

    @Override
    public void update(Message message) {

    }

    @Override
    public void delete(UUID messageId) {
        data.removeIf(message -> message.getId().equals(messageId));
    }
}