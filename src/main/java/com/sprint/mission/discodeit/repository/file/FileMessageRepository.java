package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "Messages");
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
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
        createDirectory(DIRECTORY);
        Path path = makePath(message.getId());
        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message loadMessages(Path path) {
        if (Files.notExists(path)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File io Error");
        }
    }

    @Override
    public Message findById(UUID id) {
        return loadMessages(makePath(id));
    }

    @Override
    public List<Message> findAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)){
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
    public void delete(UUID id) {
        try{
            Files.delete(makePath(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
