package com.sprint.mission.discodeit.repository.fileimpl;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                fileDirectory,
                ReadStatus.class.getSimpleName()
        );

        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + DIRECTORY, e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save read status with id " + readStatus.getId(), e);
        }

        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatusNullable = null;
        Path path = resolvePath(id);

        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                readStatusNullable = (ReadStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to read read status with id " + id, e);
            }
        }

        return Optional.ofNullable(readStatusNullable);
    }

    @Override
    public List<ReadStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Failed to read read status file: " + path.getFileName(), e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read all read status files from directory: " + DIRECTORY, e);
        }
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(status ->
                        status.getUserId().equals(userId)
                                && status.getChannelId().equals(channelId)
                )
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete read status with id " + id, e);
        }
    }
}