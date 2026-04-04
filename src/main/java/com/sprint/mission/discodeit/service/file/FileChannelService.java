package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final Path filePath;

    public FileChannelService() {
        Path directory = Paths.get(System.getProperty("user.dir"), "data");

        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.filePath = directory.resolve("channels.ser");
        this.data = load();
    }

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        save();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        Channel existing = data.get(channel.getId());

        if (existing == null) {
            return null;
        }
        existing.update(
                channel.getName()
        );
        save();
        return existing;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        save();
    }

    private void save() {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<UUID, Channel> load() {
        if (Files.exists(filePath)) {
            try (
                    FileInputStream fis = new FileInputStream(filePath.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    ) {
                return (Map<UUID, Channel>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new HashMap<>();
        }
    }
}
