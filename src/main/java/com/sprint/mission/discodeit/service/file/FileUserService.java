package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.exception.FileStorageException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Service
public class FileUserService implements UserService {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            throw new FileStorageException("유저 서비스 디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        Path path = resolvePath(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new FileStorageException("유저 생성 중 파일 저장 실패", e);
        }
        return user;
    }

    @Override
    public User find(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new FileStorageException("유저 조회 중 파일 읽기 실패", e);
            }
        }
        throw new NoSuchElementException("User with id " + id + " not found");
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            paths.filter(p -> p.toString().endsWith(EXTENSION)).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    users.add((User) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new FileStorageException("유저 목록 조회 중 파일 읽기 실패", e);
                }
            });
        } catch (IOException e) {
            throw new FileStorageException("유저 디렉토리 접근 실패", e);
        }
        return users;
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        User user = find(id);
        user.update(username, email, password);
        Path path = resolvePath(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new FileStorageException("유저 업데이트 중 파일 저장 실패", e);
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FileStorageException("유저 삭제 중 파일 삭제 실패", e);
        }
    }
}