package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileSerialization;

import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private static final String FILE_PATH = "message.ser";

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
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message delete(UUID id) {
        Message message = data.remove(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지 없음.");
        }

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());
        return message;
    }
}
