package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.exception.FileStorageException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Repository
@Primary
public class FileChannelRepository implements ChannelRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            throw new FileStorageException("채널 데이터 디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new FileStorageException("채널 데이터 파일 저장 실패", e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return Optional.ofNullable((Channel) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new FileStorageException("채널 데이터 파일 읽기 실패", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            paths.filter(p -> p.toString().endsWith(EXTENSION)).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    channels.add((Channel) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new FileStorageException("채널 데이터 파일 읽기 실패", e);
                }
            });
        } catch (IOException e) {
            throw new FileStorageException("채널 데이터 디렉토리 읽기 실패", e);
        }
        return channels;
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new FileStorageException("채널 데이터 파일 삭제 실패", e);
        }
    }
}