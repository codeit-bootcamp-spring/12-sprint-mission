package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "my_dir", "users");
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void save(User user) {
        Path path = makePath(user.getId());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.out.println("저장에 실패하였습니다.");
        }
    }

    public User findById(UUID id) {
        Path path = makePath(id);

        if (Files.notExists(path)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (User) ois.readObject();
        } catch (Exception e) {
            System.out.println("해당 유저가 존재하지 않습니다.");
            return null;
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIRECTORY,
                path -> path.toString().endsWith(EXTENSION))) {
            for (Path path : stream) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    users.add((User) ois.readObject());
                } catch (Exception e) {
                    System.out.println("일부 유저를 불러오지 못했습니다.");
                }
            }
        } catch (IOException e) {
            System.out.println("유저 목록 조회 실패");
        }
        return users;
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("유저 삭제에 실패하였습니다.");
        }
    }
}
