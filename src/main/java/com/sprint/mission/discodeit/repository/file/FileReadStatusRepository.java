package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.FileStorageException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String baseDir) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), baseDir, ReadStatus.class.getSimpleName());
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            throw new FileStorageException("ReadStatus 디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new FileStorageException("ReadStatus 파일 저장 실패", e);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return Optional.ofNullable((ReadStatus) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new FileStorageException("ReadStatus 파일 읽기 실패", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAll() {
        List<ReadStatus> list = new ArrayList<>();
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            paths.filter(p -> p.toString().endsWith(EXTENSION)).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    list.add((ReadStatus) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new FileStorageException("ReadStatus 파일 읽기 실패", e);
                }
            });
        } catch (IOException e) {
            throw new FileStorageException("ReadStatus 디렉토리 읽기 실패", e);
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
            throw new FileStorageException("ReadStatus 파일 삭제 실패", e);
        }
    }
}