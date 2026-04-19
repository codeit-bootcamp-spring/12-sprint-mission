package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;

    private final Path DIRECTORYPATH;
    private final String EXTENSION = ".ser";

    public FileMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;

        this.DIRECTORYPATH = Paths.get(System.getProperty("user.dir"), "data", "Message");
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
        if (userService.findById(message.getMemberId()) == null) {
            throw new NoSuchElementException("존재하지 않는 유저 ID 입니다.");
        }
        if (channelService.findById(message.getChannelId()) == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID 입니다.");
        }
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
            return null;
        }
        try (FileInputStream fis = new FileInputStream(targetPath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return Optional.ofNullable((Message) ois.readObject());
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
        Optional<Message> found = findById(id);
        if (found.isPresent()) {
            found.get().update(message.getContent(), attachmentIds);
            return save(found.orElse(null));
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
