package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.exception.FileStorageException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Repository
@Primary
public class FileMessageRepository implements MessageRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            throw new FileStorageException("메세지 데이터 디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        Path path = resolvePath(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new FileStorageException("메세지 데이터 파일 저장 실패", e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return Optional.ofNullable((Message) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new FileStorageException("메세지 데이터 파일 읽기 실패", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            paths.filter(p -> p.toString().endsWith(EXTENSION)).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    messages.add((Message) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new FileStorageException("메세지 데이터 파일 읽기 실패", e);
                }
            });
        } catch (IOException e) {
            throw new FileStorageException("메세지 데이터 디렉토리 읽기 실패", e);
        }
        return messages;
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
            throw new FileStorageException("메세지 데이터 파일 삭제 실패", e);
        }
    }
}