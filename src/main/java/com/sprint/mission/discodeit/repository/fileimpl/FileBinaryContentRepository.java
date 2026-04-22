package com.sprint.mission.discodeit.repository.fileimpl;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory}") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                fileDirectory,
                BinaryContent.class.getSimpleName()
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
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = resolvePath(binaryContent.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save binary content with id " + binaryContent.getId(), e);
        }

        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        BinaryContent binaryContentNullable = null;
        Path path = resolvePath(id);

        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                binaryContentNullable = (BinaryContent) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to read binary content with id " + id, e);
            }
        }

        return Optional.ofNullable(binaryContentNullable);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .distinct()
                .map(this::findById)
                .flatMap(Optional::stream)
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
            throw new RuntimeException("Failed to delete binary content with id " + id, e);
        }
    }
}