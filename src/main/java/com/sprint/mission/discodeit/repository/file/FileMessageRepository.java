package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private final String filePath = "message.ser";
    private final Map<UUID, Message> messageMap = new HashMap<>();

    public FileMessageRepository() {
        messageMap.putAll(loadFromFile());
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public void delete(UUID id) {
        messageMap.remove(id);
        saveToFile();
    }
    private Map<UUID, Message> loadFromFile() {
        File file = new File("message.ser");
        if (!file.exists()) return new HashMap<>();
        {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (Map<UUID, Message>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(messageMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
