package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "messages");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        Path path = makePath(message.getId());
        FileUtils.saveObject(path, message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        Path path = makePath(messageId);
        return (Message) FileUtils.loadObject(path);
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Message) FileUtils.loadObject(path))
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("메시지 리스트 불러오기 실패", e);
        }
    }

    @Override
    public void delete(UUID messageId) {
        Path path = makePath(messageId);
        if (!Files.exists(path)) {
            throw new NoSuchElementException("삭제할 메시지 없음: " + messageId);
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패: " + messageId, e);
        }
    }
}