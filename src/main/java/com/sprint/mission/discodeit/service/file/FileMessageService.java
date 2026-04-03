package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private final String filename = "messages.data";
    private Map<UUID, Message> data;

    public FileMessageService() {
        this.data = readFromFile();
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        writeToFile();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, String content) {
        Message message = data.get(id);
        if(message == null){
            throw new RuntimeException("메시지 없음");
        }
        message.update(content);
        writeToFile();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        writeToFile();
    }

    private void writeToFile() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, Message> readFromFile() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(filename))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
