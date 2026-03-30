package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final String FILE_PATH = "user.dat";

    public FileUserRepository() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            saveAll(new ArrayList<>());
        }
    }

    private void saveAll(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<User> loadAll() {
        // 생성자에서 파일 생성하는데 굳이 필요한가?
//        File file = new File(FILE_PATH);
//        if (!file.exists() || file.length() == 0) return new ArrayList<>();

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
    public Optional<User> findById(UUID id) {
        return loadAll().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
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
}
