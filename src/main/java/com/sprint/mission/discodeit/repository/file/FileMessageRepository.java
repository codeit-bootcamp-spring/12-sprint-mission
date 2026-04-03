package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "messages.dat";

    public FileMessageRepository() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            saveAll(new ArrayList<>());
        }
    }

    private void saveAll(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Message save(Message message) {
        List<Message> messages = loadAll();
        messages.add(message);
        saveAll(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return loadAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return loadAll();
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId) {
        List<Message> messages = loadAll();
        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                message.update(content, channelId);
                saveAll(messages);
                return message;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID messageId) {
        List<Message> messages = loadAll();
        Message target = messages.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElse(null);

        if (target != null) {
            messages.remove(target);
            saveAll(messages);
        }
    }
}
