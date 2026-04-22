package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "users");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }


    @Override
    public User save(User user) {
        Path path = makePath(user.getId());
        boolean result = FileUtils.saveObject(path, user);
        if (!result) {
            throw new IllegalStateException("Could not save user");
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = (User) FileUtils.loadObject(makePath(id));
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        for (User user : findAll()) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : findAll()) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (User) FileUtils.loadObject(path))
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException("경로를 찾을 수 없습니다.");
        }
    }


    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }
}
