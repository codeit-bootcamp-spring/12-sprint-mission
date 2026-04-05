package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "my_dir", "messages");
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void save(Message message) {
        Path path = makePath(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            System.out.println("메시지 저장에 실패하였습니다.");
        }
    }

    @Override
    public Message findById(UUID id) {
        Path path = makePath(id);
        if (Files.notExists(path)) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Message) ois.readObject();
        } catch (Exception e) {
            System.out.println("해당 메시지가 존재하지 않습니다.");
            return null;
        }
    }

    private List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIRECTORY,
                path -> path.toString().endsWith(EXTENSION))) {
            for (Path path : stream) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    messages.add((Message) ois.readObject());
                } catch (Exception e) {
                    System.out.println("일부 메시지를 불러오지 못했습니다.");
                }
            }
        } catch (IOException e) {
            System.out.println("메시지 목록 조회 실패");
        }
        return messages;
    }
    
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : findAll()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void update(Message message) {
        save(message);
    }

    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("메시지 삭제에 실패하였습니다.");
        }
    }
}