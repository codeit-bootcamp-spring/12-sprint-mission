package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserService implements UserService {
    private final Map<UUID, User> data;
    private final Path filePath;

    public FileUserService() {
        Path directory = Paths.get(System.getProperty("user.dir"), "data");

        // 저장할 경로의 파일 초기화
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.filePath = directory.resolve("users.ser");
        this.data = load();
    }

    @Override
    public User create(User user) {
        data.put(user.getId(), user);
        save();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, User user) {
        User existing = data.get(id);

        if (existing == null) {
            return null;
        }
        existing.update(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname()
        );
        save();
        return existing;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        save();
    }

    private void save() {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<UUID, User> load() {
        if (Files.exists(filePath)) {
            try (
                    FileInputStream fis = new FileInputStream(filePath.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    ) {
                return (Map<UUID, User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new HashMap<>();
        }
    }
}
