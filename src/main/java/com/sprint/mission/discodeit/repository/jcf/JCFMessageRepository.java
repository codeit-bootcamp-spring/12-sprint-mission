package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);

        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<Message> findByContent(String content) {
        for (Message message : data.values()) {
            if (message.getContent().equals(content)) {
                return Optional.of(message);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message delete(UUID id) {
        Message message = data.remove(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지 없음.");
        }

        return message;
    }
}

