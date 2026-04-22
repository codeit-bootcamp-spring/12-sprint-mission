package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Repository("fileUserStatusRepository")
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "userStatuses");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = makePath(userStatus.getId());
        boolean result = FileUtils.saveObject(path, userStatus);
        if (!result) {
            throw new IllegalArgumentException("UserStatus 저장 실패");
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable((UserStatus) FileUtils.loadObject(makePath(id)));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        List<UserStatus> userStatusList = findAll();
        for (UserStatus userStatus : userStatusList) {
            if (userStatus.getUserId().equals(userId)) {
                return Optional.of(userStatus);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return FileUtils.load(DIRECTORY);
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = makePath(id);
        return Files.exists(path);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        List<UserStatus> userStatusList = findAll();
        for (UserStatus userStatus : userStatusList) {
            if (userStatus.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            throw new RuntimeException("삭제 실패: 해당 " + id + "의 파일을 찾을 수 없습니다.");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<UserStatus> userStatusList = findAll();
        for (UserStatus userStatus : userStatusList) {
            if (!userStatus.getUserId().equals(userId)) {
                continue;
            }
            Path path = makePath(userStatus.getId());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("파일을 삭제할 수 없습니다. userStatusId: " + userStatus.getId());
            }
        }
    }
}
