package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private static final String FILE_PATH = "messages.ser";

    private final Map<UUID, Message> messageRepo;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.messageRepo = loadFromFile();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message send(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메시지가 null입니다.");
        }

        if (message.getContent() == null || message.getContent().isBlank()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }

        User user = message.getSender();
        if (user == null || userService.getUser(user.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Channel channel = message.getChannel();
        if (channel == null || channelService.getChannel(channel.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        messageRepo.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return messageRepo.get(id);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();

        for (Message message : messageRepo.values()) {
            if (message.getChannel().getId().equals(channelId)) {
                result.add(message);
            }
        }

        return result;
    }

    @Override
    public Message edit(UUID id, String content) {
        Message message = messageRepo.get(id);

        if (message != null) {
            message.updateContent(content);
            saveToFile();
        }

        return message;
    }

    @Override
    public void remove(UUID id) {
        messageRepo.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messageRepo);
        } catch (IOException e) {
            throw new RuntimeException("Message 저장 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Message 로드 실패", e);
        }
    }
}