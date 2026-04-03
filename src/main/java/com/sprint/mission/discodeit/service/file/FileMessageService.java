package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final String FILE_PATH = "messages.dat";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;

        File file = new File(FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            saveAll(new ArrayList<>());
        }
    }

    private void saveAll(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Message save(Message message) {
        if (message == null ||
                userService.findById(message.getUserId()) == null ||
                channelService.findById(message.getChannelId()) == null) {
            return null;
        }

        List<Message> messages = loadAll();
        messages.add(message);
        saveAll(messages);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return loadAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return loadAll();
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId) {
        List<Message> messages = loadAll();
        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                message.update(content, channelId);
                saveAll(messages);
                return message;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID messageId) {
        List<Message> messages = loadAll();
        Message target = messages.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElse(null);

        if (target != null) {
            messages.remove(target);
            saveAll(messages);
        }
    }
}