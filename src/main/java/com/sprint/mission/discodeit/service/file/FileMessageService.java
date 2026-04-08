package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private final String BASEPATH = "./persistentfiles/messages";

    public FileMessageService() {
        File file = new File(BASEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public UUID create(Message message) {
        saveToFile(message);
        return message.getId();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(loadFromFile(id.toString()));
    }

    @Override
    public Optional<List<Message>> findBySendUser(User user) {
        List<Message> list = new ArrayList<>();
        findAll().ifPresent(
                (messages) -> {
                    for (Message message : messages) {
                        if (message.getSendUser().equals(user)) {
                            list.add(message);
                        }
                    }
                }
        );
        return Optional.of(list);
    }

    @Override
    public Optional<List<Message>> findAll() {
        return Optional.ofNullable(loadAllFromFile());
    }

    @Override
    public void updateById(UUID id, String content) {
        findById(id).ifPresentOrElse(
                message -> {
                    message.update(content);
                    saveToFile(message);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Message가 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresentOrElse(
                message -> {
                    deleteFile(message.getId().toString());
                },
                () -> System.out.println("\t삭제실패 : 입력된 id(" + id + ")에 해당하는 Message가 없습니다")
        );
    }


    private void saveToFile(Message message) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BASEPATH + "/" + message.getId().toString()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Message loadFromFile(String id) {
        File file = new File(BASEPATH + "/" + id);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<Message> loadAllFromFile() {
        List<Message> list = new ArrayList<>();
        File dir = new File(BASEPATH);
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                list.add((Message) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    private void deleteFile(String id) {
        File file = new File(BASEPATH + "/" + id);
        if (!file.exists()) {
            System.out.println(file.toPath() + " 파일이 없으므로 삭제할 수 없습니다");
            throw new RuntimeException();
        }
        file.delete();
    }
}
