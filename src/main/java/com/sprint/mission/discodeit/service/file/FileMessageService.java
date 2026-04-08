package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final Path filePath;
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;

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
        if (channelService.findById(message.getChannelId()).isEmpty()) {
            return null;
        }

        if (userService.findById(message.getUserId()).isEmpty()) {
            return null;
        }

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
            return null;
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
        } else {
            return new HashMap<>();
        }
    }
}