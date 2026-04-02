package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "users");
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User save(User user) {
        Path path = makePath(user.getId());
        boolean result;
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        if (!result) {
            throw new IllegalStateException("User를 저장할 수 없습니다.");
        }
        return user;
    }

    public User loadUsers(Path path) {
        if (Files.notExists(path)) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 입출력 에러 발생");
        }
    }

    @Override
    public User findById(UUID id) {
        Path path = makePath(id);
        return loadUsers(path);
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
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
    public User update(User user) {
        Path path = makePath(user.getId());
        User oldUser = loadUsers(path);
        if(oldUser == null){
            return null;
        }
        oldUser.update(user);
        save(oldUser);
        return oldUser;
    }

    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        if (Files.notExists(path)) {
            return;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }
}
