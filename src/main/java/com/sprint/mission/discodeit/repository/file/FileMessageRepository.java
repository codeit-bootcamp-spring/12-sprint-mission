package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String BASEPATH = "./persistentfiles/messages";

    public FileMessageRepository() {
        File file = new File(BASEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void save(Message message) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BASEPATH + "/" + message.getId().toString()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        File file = new File(BASEPATH + "/" + id.toString());
        if (!file.exists()) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return Optional.of((Message) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<Message>> findAll() {
        List<Message> list = new ArrayList<>();
        File dir = new File(BASEPATH);
        File[] files = dir.listFiles();
        if (files == null) {
            return Optional.empty();
        }
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                list.add((Message) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return Optional.of(list);
    }

    @Override
    public void deleteById(UUID id) {
        File file = new File(BASEPATH + "/" + id.toString());
        if (!file.exists()) {
            System.out.println(file.toPath() + " 파일이 없으므로 삭제할 수 없습니다");
            return;
        }
        file.delete();
    }
}
