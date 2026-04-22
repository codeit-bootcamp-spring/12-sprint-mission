package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "binaryContents");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = makePath(binaryContent.getId());
        boolean result = FileUtils.saveObject(path, binaryContent);
        if (!result) {
            throw new IllegalStateException("첨부파일을 저장할 수 없습니다.");
        }
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        BinaryContent binaryContent = (BinaryContent) FileUtils.loadObject(makePath(id));
        return Optional.ofNullable(binaryContent);
    }

    @Override
    public List<BinaryContent> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (BinaryContent) FileUtils.loadObject(path))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("첨부파일 목록을 조회할 수 없습니다.");
        }
    }

    @Override
    public List<BinaryContent> findByIdIn(UUID id) {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (BinaryContent) FileUtils.loadObject(path))
                    .filter(binaryContent -> binaryContent.getId().equals(id))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("첨부파일 목록을 조회할 수 없습니다.");
        }
    }

    @Override
    public List<BinaryContent> findAllByMessageId(UUID messageId) {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (BinaryContent) FileUtils.loadObject(path))
                    .filter(binaryContent -> binaryContent.getMessageId().equals(messageId))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("메시지 기준 첨부파일 목록을 조회할 수 없습니다.");
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
            throw new RuntimeException("첨부파일을 삭제할 수 없습니다.");
        }
    }
}