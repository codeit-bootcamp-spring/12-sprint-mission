package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelCategory;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelService() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if (!Files.exists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {return DIRECTORY.resolve(id.toString() + EXTENSION);}

    @Override
    public Channel create(ChannelCategory category, String name, String description) {
        Channel channel = new Channel(category, name, description);
        Path path = resolvePath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
             oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return channel;

    }

    @Override
    public Channel read(UUID ChannelId) {
        Channel channelNullable = null;
        Path path = resolvePath(ChannelId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + ChannelId + " not found"));
    }

    @Override
    public List<Channel> readAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channelNullable = null;
        Path path = resolvePath(channelId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Channel channel = Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channel.update(newName, newDescription);

        try (

                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }
        @Override
    public void delete(UUID channelId) {
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
