package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    public FileMessageRepository(@Value("${storage.location}") String storageLocation) {
        DIRECTORY = Path.of(storageLocation,"Messages");
        createDirectory(DIRECTORY);
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
    public Message save(Message message) {
        if (message == null) throw new NoSuchElementException("Message 객체가 비어있습니다.");
        if (message.getId() == null) throw new IllegalArgumentException("Message ID를 찾을 수 없습니다.");
        if (message.getUserId() == null) throw new IllegalArgumentException("Message의 작성자 정보가 누락되었습니다.");
        if (message.getChannelId() == null) throw new IllegalArgumentException("Message의 채널 정보가 누락되었습니다.");

        Path path = makePath(message.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
            return message;
        } catch (IOException e) {
            return null;
        }
    }

    public Message loadMessage(Path path) {
        if (Files.notExists(path) || Files.isDirectory(path)) return null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object obj = ois.readObject();
            if(!(obj instanceof Message)){
                throw new IllegalArgumentException("파일 내용이 Message가 아닙니다.: " + path);
            }
            return (Message) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Message 파일 로드 실패 : " + path);
            return null;
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(loadMessage(makePath(id)));
    }

    @Override
    public List<Message> findAll() {
        if(!Files.isDirectory(DIRECTORY)) return Collections.emptyList();
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadMessage)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Message::getUpdatedAt))
                    .toList();
        } catch (IOException e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public List<Message> findByChannelId(UUID id) {
        return findAll().stream()
                .filter(message -> message.getChannelId().equals(id))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return loadMessage(makePath(id)) != null;
    }

    @Override
    public void delete(UUID id) {
        try {
            boolean deleted = Files.deleteIfExists(makePath(id));
            if (!deleted) {
                System.out.println("삭제 실패 : 해당 ID의 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }
}
