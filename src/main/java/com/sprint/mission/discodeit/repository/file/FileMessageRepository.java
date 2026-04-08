package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository  {
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
        try {
            FileUtils.saveObject(path, message);
        } catch (RuntimeException e) {
            throw new RuntimeException("메시지 저장 실패");
        }
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return (Message) FileUtils.loadObject(makePath(messageId));
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
            throw new NoSuchElementException("경로를 찾을 수 없음");
        }
    }

    @Override
    public void update(Message message) {
        save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Path path = makePath(messageId);

        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패");
        }
    }
}