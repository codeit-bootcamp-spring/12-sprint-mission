package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORYPATH;
    private final String EXTENSION = ".ser";
    private final String CURRENTDIR = "user.dir";

    public FileMessageRepository(){
        this.DIRECTORYPATH = Paths.get(System.getProperty(CURRENTDIR), "data", "Message");
        if (!Files.exists(DIRECTORYPATH)) {
            try {
                Files.createDirectories(DIRECTORYPATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORYPATH.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        Path targetPath = makePath(message.getId());
        try(
                FileOutputStream fos = new FileOutputStream(targetPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path targetPath = makePath(id);
        if (!Files.exists(targetPath)) {
            return Optional.empty();
        }
        try (FileInputStream fis = new FileInputStream(targetPath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Message message = (Message) ois.readObject();
            return Optional.ofNullable(message);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> findAll() {
        if (Files.exists(DIRECTORYPATH)) {
            try {
                List<Message> list = Files.list(DIRECTORYPATH)
                        .filter(path -> path.toString().endsWith(EXTENSION))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Message) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return list;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Message update(UUID id, Message message, List<UUID> attachmentIds) {
        Optional<Message> OptionalMessage = findById(id);
        if (OptionalMessage.isPresent()) {
            Message found = OptionalMessage.get();
            found.update(message.getContent(), attachmentIds);
            return save(found);
        }
        return null;
    }

    @Override
    public boolean delete(UUID id) {
        Path targetPath = makePath(id);
        try {
            return Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
