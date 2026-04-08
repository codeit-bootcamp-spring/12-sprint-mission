package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final File file;

    public FileMessageRepository() {
        this.file = new File("messages.dat");
    }

    @Override
    public Message save(Message message) {
        List<Message> messages = readAll();

        boolean updated = false;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                messages.set(i, message);
                updated = true;
                break;
            }
        }
        if (!updated) {
            messages.add(message);
        }
        writeAll(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return readAll().stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return readAll();
    }

    @Override
    public void delete(UUID id) {
        List<Message> messages = readAll();
        messages.removeIf(message -> message.getId().equals(id));
        writeAll(messages);
    }

    @SuppressWarnings("unchecked")
    private List<Message> readAll() {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 파일 읽기에 실패했습니다.", e);
        }
    }

    private void writeAll(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 저장에 실패했습니다.", e);
        }
    }
}
