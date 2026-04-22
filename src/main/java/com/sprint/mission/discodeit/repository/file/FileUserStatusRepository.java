package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORYPATH;
    private final String EXTENSION = ".ser";
    private final String CURRENTDIR = "user.dir";


    public FileUserStatusRepository() {
        this.DIRECTORYPATH = Paths.get(System.getProperty(CURRENTDIR), "data", "UserStatus");
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
    public UserStatus create(UserStatus userStatus) {
        Path targetPath = makePath(userStatus.getId());
        try(
                FileOutputStream fos = new FileOutputStream(targetPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path targetPath = makePath(id);
        if (!Files.exists(targetPath)) {
            return Optional.empty();
        }
        try (FileInputStream fis = new FileInputStream(targetPath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            UserStatus userStatus = (UserStatus) ois.readObject();
            return Optional.ofNullable(userStatus);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserStatus update(UUID id, UserStatus userStatus) {
        Optional<UserStatus> OptionalUserStatus = findById(id);
        if (OptionalUserStatus.isPresent()) {
            UserStatus found = OptionalUserStatus.get();
            found.update();
            return create(found);
        }
        return null;
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
