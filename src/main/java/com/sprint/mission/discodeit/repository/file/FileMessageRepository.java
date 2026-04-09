package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository("fileMessageRepository")
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository(){
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "Messages");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id){
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    @Override
    public Message save(Message message) {
        Path path = makePath(message.getId());
        boolean result = FileUtils.saveObject(path, message);
        if(!result){
            throw new IllegalStateException("저장에 실패했습니다.");
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable((Message)FileUtils.loadObject(makePath(id)));
    }

    @Override
    public List<Message> findAll() {
        return FileUtils.load(DIRECTORY);
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = makePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = makePath(id);
        if(!Files.exists(path)){
            throw new RuntimeException("삭제 실패: 해당 " + id + "의 파일을 찾을 수 없습니다.");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }
}
