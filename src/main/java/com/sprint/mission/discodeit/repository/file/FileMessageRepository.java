package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;
    private final Path filePath;

    public FileMessageRepository() {
        Path directory = Paths.get(System.getProperty("user.dir"), "data");

        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.filePath = directory.resolve("messages.ser");
        this.data = load();
    }

    @Override
    public Message create(Message message) {
        data.put(message.getId(), message);
        save();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(Message message) {
        if (!data.containsKey(message.getId())) {
            throw new NoSuchElementException("Message not found: " + message.getId());
        }

        data.put(message.getId(), message);
        save();
        return message;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        save();
    }

    private void save() {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<UUID, Message> load() {
        if (Files.exists(filePath)) {
            try (
                    FileInputStream fis = new FileInputStream(filePath.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                return (Map<UUID, Message>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return new HashMap<>();
    }
}