package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "userStatuses");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = makePath(userStatus.getId());
        boolean result = FileUtils.saveObject(path, userStatus);
        if (!result) {
            throw new IllegalStateException("Could not save user status");
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        UserStatus userStatus = (UserStatus) FileUtils.loadObject(makePath(id));
        return Optional.ofNullable(userStatus);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus userStatus : findAll()) {
            if (userStatus.getUserId().equals(userId)) {
                return Optional.of(userStatus);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (UserStatus) FileUtils.loadObject(path))
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException("상태를 목록을 조회할 수 없습니다.");
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
            throw new RuntimeException("상태를 삭제할 수 없습니다.");
        }
    }
}
