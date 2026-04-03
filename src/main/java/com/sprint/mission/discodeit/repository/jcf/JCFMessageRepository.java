package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        if (message == null) {;
            return null;
        }

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId) {
        Message message = findById(messageId).orElse(null);

        if (message != null) {
            message.update(content, channelId);
        }

        return message;
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }
}
