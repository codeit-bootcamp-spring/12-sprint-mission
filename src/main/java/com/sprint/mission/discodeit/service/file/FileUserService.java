package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {
    private final String FILE_PATH = "user.dat";

    public FileUserService() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            saveAll(new ArrayList<>());
        }
    }

    // 리스트 전체를 파일에 쓰기
    private void saveAll(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

    // 파일에서 리스트 전체 읽어오기
    @SuppressWarnings("unchecked")
    private List<User> loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public User save(User user) {
        List<User> users = loadAll();
        users.add(user);
        saveAll(users);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return loadAll().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return loadAll();
    }

    @Override
    public User update(UUID id, String name, String email, String password, String nickname) {
        List<User> users = loadAll();
        for (User user : users) {
            if (user.getId().equals(id)) {
                user.update(name, email, password, nickname);
                saveAll(users);
                return user;
            }
        }
        return null;
    }

    @Override
    public User delete(UUID id) {
        List<User> users = loadAll();
        User target = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (target != null) {
            users.remove(target);
            saveAll(users);
        }
        return target;
    }

    public boolean isNicknameUnique(String nickname) {
        return loadAll().stream()
                .noneMatch(user -> user.getNickname().equals(nickname));
    }

    public User createAndSaveUser(String username, String email, String password, String nickname) {
        if(username == null || email == null || password == null || nickname == null || !isNicknameUnique(nickname)){
            return null;
        }

        return save(new User(username, email, password, nickname));
    }
}