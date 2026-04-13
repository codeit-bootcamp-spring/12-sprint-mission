package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Service
public class FileMessageService implements MessageService {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(
            @Qualifier("basicChannelService") ChannelService channelService,
            @Qualifier("basicUserService") UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            throw new FileStorageException("메세지 서비스 디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        channelService.find(channelId);
        userService.find(authorId);

        Message message = new Message(content, channelId, authorId);
        Path path = resolvePath(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new FileStorageException("메세지 생성 중 파일 저장 실패", e);
        }
        return message;
    }

    @Override
    public Message find(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new FileStorageException("메세지 조회 중 파일 읽기 실패", e);
            }
        }
        throw new NoSuchElementException("Message with id " + id + " not found");
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            paths.filter(p -> p.toString().endsWith(EXTENSION)).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    messages.add((Message) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new FileStorageException("메세지 목록 조회 중 파일 읽기 실패", e);
                }
            });
        } catch (IOException e) {
            throw new FileStorageException("메세지 디렉토리 접근 실패", e);
        }
        return messages;
    }

    @Override
    public Message update(UUID id, String newContent) {
        Message message = find(id);
        message.update(newContent);
        Path path = resolvePath(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new FileStorageException("메세지 업데이트 중 파일 저장 실패", e);
        }
        return message;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FileStorageException("메세지 삭제 중 파일 삭제 실패", e);
        }
    }
}