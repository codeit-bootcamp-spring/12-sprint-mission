package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.FileStorageException;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String baseDir) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), baseDir, UserStatus.class.getSimpleName());
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            throw new FileStorageException("UserStatus 디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new FileStorageException("UserStatus 파일 저장 실패", e);
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return Optional.ofNullable((UserStatus) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new FileStorageException("UserStatus 파일 읽기 실패", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        List<UserStatus> list = new ArrayList<>();
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            paths.filter(p -> p.toString().endsWith(EXTENSION)).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    list.add((UserStatus) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new FileStorageException("UserStatus 파일 읽기 실패", e);
                }
            });
        } catch (IOException e) {
            throw new FileStorageException("UserStatus 디렉토리 읽기 실패", e);
        }
        return list;
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new FileStorageException("UserStatus 파일 삭제 실패", e);
        }
    }
}