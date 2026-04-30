package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)

public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path baseDir = Paths.get("data/binary-content");

    public FileBinaryContentRepository() {
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path filePath = getPath(binaryContent.getId());

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path filePath = getPath(id);

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(filePath))) {
            return Optional.of((BinaryContent) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 조회 실패", e);
        }
    }

    @Override
    public List<BinaryContent> findAll(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(getPath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(getPath(id));
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

    private Path getPath(UUID id) {
        return baseDir.resolve(id + ".bin");
    }

}
