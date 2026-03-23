package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private static final String FILE_PATH = "message.ser";

    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message findMessageByContent(String content) {
        for (Message message : data.values()) {
            if (message.getContent().equals(content)) {
                return message;
            }
        }

        throw new IllegalArgumentException("메시지 없음.");
    }

    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message changeMessageContent(UUID id, String content) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지 없음.");
        }

        message.update(message.getAuthorId(), message.getChannelId(), content);

        return message;
    }

    @Override
    public Message deleteMessage(UUID id) {
        Message message = data.remove(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지 없음.");
        }

        return message;
    }
}

