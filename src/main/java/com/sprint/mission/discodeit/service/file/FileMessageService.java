package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageService implements MessageService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageService(){
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "Messages");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id){
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        Message message = new Message(content, channelId, authorId);
        Path path = makePath(message.getId());
        boolean result = FileUtils.saveObject(path, message);
        if(!result){
            throw new IllegalStateException("message 저장에 실패했습니다.");
        }
        return message;
    }

     @Override
    public Message find(UUID messageId) {
         return (Message)FileUtils.loadObject(makePath(messageId));
    }

    @Override
    public List<Message> findAll() {
        return FileUtils.load(DIRECTORY);
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        return null;
    }


    @Override
    public void delete(UUID id) {
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

    public void deleteAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)) {
            stream.filter(path ->  path.getFileName().toString().endsWith(EXTENSION))
                    .forEach(path ->{
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
