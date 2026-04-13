package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {
    private static final String FILE_PATH = "Message.ser";

    private final Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = new HashMap<>();

        List<Message> messageList = FileSerialization.loadData(FILE_PATH);
        for (Message msg : messageList) {
            data.put(msg.getId(), msg);
        }
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

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

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

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

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return deletedMessageList;
    }
}
