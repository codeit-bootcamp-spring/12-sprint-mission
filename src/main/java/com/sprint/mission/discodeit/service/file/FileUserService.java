package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "users");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User save(User user) {
        Path path = makePath(user.getId());
        try {
            FileUtils.saveObject(path, user);
        } catch (RuntimeException e) {
            throw new RuntimeException("사용자 저장 실패");
        }
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return (User) FileUtils.loadObject(makePath(userId));
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
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없음");
        }
    }

    @Override
    public void update(UUID userId, String username, String email, String password, String nickname) {
        User user = findById(userId);
        user.update(username, email, password, nickname);

        Path path = makePath(userId);
        try {
            FileUtils.saveObject(path, user);
        } catch (RuntimeException e) {
            throw new RuntimeException("사용자 정보 변경 실패");
        }
    }

    @Override
    public void delete(UUID userId) {
        Path path = makePath(userId);

        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("사용자 삭제 실패");
        }
    }
}
