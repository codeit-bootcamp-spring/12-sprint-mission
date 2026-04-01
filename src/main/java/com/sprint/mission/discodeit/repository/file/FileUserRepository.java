package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "users");
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    public void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public User save(User user) {
        createDirectory(DIRECTORY);
        Path path = makePath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User loadUsers(Path path) {
        if (Files.notExists(path)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File io Error");
        }
    }

    @Override
    public User findById(UUID id) {
        return loadUsers(makePath(id));
    }

    @Override
    public List<User> findAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadUsers)
                    .sorted()
                    .toList();
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            Files.delete(makePath(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
