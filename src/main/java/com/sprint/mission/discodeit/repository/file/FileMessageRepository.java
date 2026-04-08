package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final File dir = new File("file");
    private final File messageFile = new File("file", "message.dat");

    public FileMessageRepository() {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            if (!messageFile.exists()) {
                messageFile.createNewFile();
            }

        } catch (Exception e) {
            throw new RuntimeException("파일을 생성할 수 없습니다.");
        }
    }

    private Map<UUID, Message> readMessage() {
        if (messageFile.length() == 0) {
            return new HashMap<>();
        }
        try (FileInputStream fis = new FileInputStream(messageFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Map<UUID, Message> messageMap = (Map<UUID, Message>) ois.readObject();
            return messageMap;
        } catch (Exception e) {
            throw new RuntimeException("파일을 읽을 수 없습니다");
        }
    }

    private void writeMessage(Map<UUID, Message> messageMap) {
        try (FileOutputStream fos = new FileOutputStream(messageFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(messageMap);
        } catch (Exception e) {
            throw new RuntimeException("파일을 저장할 수 없습니다");
        }
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> messageMap = readMessage();
        messageMap.put(message.getMessageId(), message);
        writeMessage(messageMap);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Map<UUID, Message> messageMap = readMessage();
        return messageMap.get(id);
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> messageMap = readMessage();
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> messageMap = readMessage();
        messageMap.remove(id);
        writeMessage(messageMap);
    }
}
