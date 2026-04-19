package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {
    private final Path DIRECTORYPATH;
    private final String EXTENSION = ".ser";


    public FileUserService() {
        this.DIRECTORYPATH = Paths.get(System.getProperty("user.dir"), "data", "User");
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
    public User create(User user) {
        Path targetPath = makePath(user.getId());
        try(
                FileOutputStream fos = new FileOutputStream(targetPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path targetPath = makePath(id);
        if (!Files.exists(targetPath)) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(targetPath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return Optional.ofNullable((User) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        if (Files.exists(DIRECTORYPATH)) {
            try {
                List<User> list = Files.list(DIRECTORYPATH)
                        .filter(path -> path.toString().endsWith(EXTENSION))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (User) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return list;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public User update(UUID id, User user) {
        Optional<User> found = findById(id);
        if (found.isPresent()) {
            found.get().update(user.getUserName(), user.getPassword(), user.getEmail(), user.getNickName(), user.getProfileId());
            return create(found.orElse(null));
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
