package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "users");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User save(User user) {
        Path path = makePath(user.getId());
        FileUtils.saveObject(path, user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        Path path = makePath(userId);
        return (User) FileUtils.loadObject(path);
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (User) FileUtils.loadObject(path))
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("사용자 리스트 불러오기 실패");
        }
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(UUID userId) {
        Path path = makePath(userId);
        if (!Files.exists(path)) {
            throw new NoSuchElementException("삭제할 사용자 없음: " + userId);
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new NoSuchElementException("사용자 삭제 실패: " + userId, e);
        }
    }
}