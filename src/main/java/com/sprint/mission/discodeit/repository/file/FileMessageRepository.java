package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;


@Repository("messageRepository")
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"),"my_dir","messages");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id){
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        Path path = makePath(message.getId());
        boolean result = FileUtils.saveObject(path,message);
        if(!result){
            throw new IllegalStateException("Could not save message");
        }
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return (Message) FileUtils.loadObject(makePath(id));
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> stream = Files.walk(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Message)FileUtils.loadObject(path))
                    .toList();

        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public Message update(Message message) {
        Path path = makePath(message.getId());
        if(!Files.exists(path)){
            throw new NoSuchElementException("수정할 메시지가 없습니다.");
        }
        return save(message);
    }

    @Override
    public Message delete(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            return  null;
        }
        Message messageDel = (Message) FileUtils.loadObject(path);
        try {
            Files.delete(path);
            return messageDel;
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제 할 수 없습니다.");
        }
    }
}
