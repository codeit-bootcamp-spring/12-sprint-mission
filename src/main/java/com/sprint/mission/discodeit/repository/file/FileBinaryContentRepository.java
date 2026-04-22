package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORYPATH;
    private final String EXTENSION = ".ser";
    private final String CURRENTDIR = "user.dir";


    public FileBinaryContentRepository() {
        this.DIRECTORYPATH = Paths.get(System.getProperty(CURRENTDIR), "data", "BinaryContent");
        if (!Files.exists(DIRECTORYPATH)) {
            try {
                Files.createDirectories(DIRECTORYPATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORYPATH.resolve(id + EXTENSION);
    }

    @Override
    public BinaryContent create(BinaryContent binaryContent) {
        Path targetPath = makePath(binaryContent.getId());
        try(
                FileOutputStream fos = new FileOutputStream(targetPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path targetPath = makePath(id);
        if (!Files.exists(targetPath)) {
            return Optional.empty();
        }
        try (FileInputStream fis = new FileInputStream(targetPath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            BinaryContent binaryContent = (BinaryContent) ois.readObject();
            return Optional.ofNullable(binaryContent);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(UUID id) {
        Path targetPath = makePath(id);
        try {
            return Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
