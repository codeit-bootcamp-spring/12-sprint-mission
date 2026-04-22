package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "messages");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        Path path = makePath(message.getId());
        boolean result = FileUtils.saveObject(path, message);
        if (!result) {
            throw new IllegalStateException("Could not save message");
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = (Message) FileUtils.loadObject(makePath(id));
        return Optional.ofNullable(message);
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> stream = Files.walk(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Message) FileUtils.loadObject(path))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("메시지 목록을 조회할 수 없습니다.");
        }
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        try (Stream<Path> stream = Files.walk(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Message) FileUtils.loadObject(path))
                    .filter(message -> message.getChannelId().equals(channelId))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("채널으 ㅣ 메시지 목록을 조회할 수 없습니다.");
        }
    }


    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제 할 수 없습니다.");
        }
    }
}
