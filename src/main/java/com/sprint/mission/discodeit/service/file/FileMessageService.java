package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageService implements MessageService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "messages");
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        if (userService.findById(message.getAuthor().getId()) == null) {
            System.out.println("Author가 존재하지 않습니다.");
            return null;
        }
        if (channelService.findById(message.getCh().getId()) == null) {
            System.out.println("Channel이 존재하지 않습니다.");
            return null;
        }
        Path path = makePath(message.getId());
        boolean result;
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        if (!result) {
            throw new IllegalStateException("Message를 저장할 수 없습니다.");
        }
        return message;
    }

    public Message loadMessages(Path path) {
        if (Files.notExists(path)) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 입출력 에러 발생");
        }
    }

    @Override
    public Message findById(UUID id) {
        Path path = makePath(id);
        return loadMessages(path);
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadMessages)
                    .sorted()
                    .toList();
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public Message update(Message message) {
        Path path = makePath(message.getId());
        Message oldMessage = loadMessages(path);
        if(oldMessage == null){
            return null;
        }
        oldMessage.update(message);
        save(oldMessage);
        return oldMessage;
    }

    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        if (Files.notExists(path)) {
            return;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }
}
