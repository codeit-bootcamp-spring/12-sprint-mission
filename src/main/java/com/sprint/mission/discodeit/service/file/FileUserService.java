package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private static final String FILE_PATH = "users.ser";
    private final Map<UUID, User> userRepo;

    public FileUserService() {
        this.userRepo = loadFromFile();
    }

    @Override
    public User create(User user) {
        userRepo.put(user.getId(), user);
        saveToFile();
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return userRepo.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userRepo.values());
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        User existingUser = userRepo.get(id);

        if (existingUser == null) {
            return null;
        }

        if (username != null) {
            existingUser.updateUsername(username);
        }

        if (email != null) {
            existingUser.updateEmail(email);
        }

        if (password != null) {
            existingUser.updatePassword(password);
        }

        saveToFile();
        return existingUser;
    }

    @Override
    public void delete(UUID id) {
        userRepo.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(userRepo);
        } catch (IOException e) {
            throw new RuntimeException("User 저장 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("User 로드 실패", e);
        }
    }
}