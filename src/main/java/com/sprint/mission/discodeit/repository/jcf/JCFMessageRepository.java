package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
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
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message deleteById(UUID id) {
        Message removed = data.remove(id);

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 Message 없음.");
        }

        return removed;
    }

    @Override
    public List<Message> deleteAllByChannelId(UUID channelId) {
        List<UUID> targetIdList = new ArrayList<>();
        List<Message> deletedMessageList = new ArrayList<>();

        for (Message msg : data.values()) {
            if (msg.getChannelId().equals(channelId)) {
                targetIdList.add(msg.getId());
            }
        }

        for (UUID id : targetIdList) {
            Message removed = data.remove(id);
            if (removed != null) {
                deletedMessageList.add(removed);
            }
        }

        return deletedMessageList;
    }
}

