package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "readStatuses");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = makePath(readStatus.getId());
        boolean result = FileUtils.saveObject(path, readStatus);
        if (!result) {
            throw new IllegalStateException("읽음 상태를 저장할 수 없습니다.");
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatus = (ReadStatus) FileUtils.loadObject(makePath(id));
        return Optional.ofNullable(readStatus);
    }

    @Override
    public List<ReadStatus> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (ReadStatus) FileUtils.loadObject(path))
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException("읽음 상태 목록을 조회할 수 없습니다.");
        }
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID id) {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (ReadStatus) FileUtils.loadObject(path))
                    .filter(readStatus -> readStatus.getUserId().equals(id))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("사용자 기준 읽음 상태 목록을 조회할 수 없습니다.");
        }
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID id) {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (ReadStatus) FileUtils.loadObject(path))
                    .filter(readStatus -> readStatus.getChannelId().equals(id))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("채널 기준 읽음 상태 목록을 조회할 수 없습니다.");
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
            throw new RuntimeException("읽음 상태 파일을 삭제할 수 없습니다.");
        }
    }
}