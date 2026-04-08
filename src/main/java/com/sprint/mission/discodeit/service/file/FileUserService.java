package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final String BASEPATH = "./persistentfiles/users";

    public FileUserService() {
        File file = new File(BASEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public UUID create(User user) {
        saveToFile(user);
        return user.getId();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(loadFromFile(id.toString()));
    }

    @Override
    public Optional<List<User>> findAll() {
        return Optional.ofNullable(loadAllFromFile());
    }

    @Override
    public void updateById(UUID id, String username, String email, String password, String nickname) {
        findById(id).ifPresentOrElse(
                (user) -> {
                    user.update(username, email, password, nickname);
                    saveToFile(user);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 User가 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresentOrElse(
                (user) -> deleteFile(user.getId().toString()),
                () -> System.out.println("\t삭제실패 : 입력된 id(" + id + ")에 해당하는 User가 없습니다")
        );
    }


    private void saveToFile(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BASEPATH + "/" + user.getId().toString()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private User loadFromFile(String id) {
        File file = new File(BASEPATH + "/" + id);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<User> loadAllFromFile() {
        List<User> list = new ArrayList<>();
        File dir = new File(BASEPATH);
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                list.add((User) ois.readObject());
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
            return;
        }
        file.delete();
    }
}
