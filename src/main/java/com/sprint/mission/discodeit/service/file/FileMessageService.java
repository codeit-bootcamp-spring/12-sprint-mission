package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final Path MESSAGE_PATH = Path.of("data","messages");

    public FileMessageService() {
        FileUtils.createDirectories(MESSAGE_PATH);
    }

    @Override
    public Message save(Message message) {
        Path path = MESSAGE_PATH.resolve(message.getId().toString() + ".ser");
        FileUtils.saveObject(path, message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Path path = MESSAGE_PATH.resolve(id.toString() + ".ser");
        return (Message) FileUtils.loadObject(path);
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        if(!Files.exists(MESSAGE_PATH)) {
            return messages;
        }
        try {
            Files.list(MESSAGE_PATH).forEach(file -> {
                messages.add((Message) FileUtils.loadObject(file));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Message update(Message message) {
        Path path = MESSAGE_PATH.resolve(message.getId().toString() + ".ser");
        if(!Files.exists(MESSAGE_PATH)) {
            throw new RuntimeException("Message not found.");
        }
        FileUtils.saveObject(path, message);
        return message;
    }

    @Override
    public Message delete(UUID id) {
        Message message = findById(id);
        Path path = MESSAGE_PATH.resolve(message.getId().toString() + ".ser");
        try {
            Files.delete(path);
            return message;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete message.");
        }
    }
}
