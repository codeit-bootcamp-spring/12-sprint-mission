package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "channels");
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    public void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        createDirectory(DIRECTORY);
        Path path = makePath(channel.getId());
        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Channel loadChannels(Path path) {
        if (Files.notExists(path)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File io Error");
        }
    }

    @Override
    public Channel findById(UUID id) {
        return loadChannels(makePath(id));
    }

    @Override
    public List<Channel> findAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)){
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadChannels)
                    .sorted()
                    .toList();
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public void delete(UUID id) {
        try{
            Files.delete(makePath(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
