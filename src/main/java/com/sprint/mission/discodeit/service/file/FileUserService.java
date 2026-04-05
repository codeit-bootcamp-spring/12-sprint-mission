package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FileUserService implements UserService {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
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
    public User createUser(String username, String email, String password, String nickname) {
        // [비즈니스 로직]
        User user = new User(username, email, password, nickname);

        // [저장 로직]
        Path path = makePath(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.out.println("저장에 실패하였습니다.");
        }
        return user;
    }

    @Override
    public User getUser(UUID id) {
        Path path = makePath(id);
        if (Files.notExists(path)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (User) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIRECTORY, path -> path.toString().endsWith(EXTENSION))) {
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
    public User updateUser(UUID id, String username, String email, String password, String nickname) {
        User user = getUser(id);
        if (user != null) {
            user.update(username, email, password, nickname); // [비즈니스 로직]

            // [저장 로직] 덮어쓰기
            Path path = makePath(id);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                oos.writeObject(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            Files.deleteIfExists(makePath(id));
        } catch (IOException e) {
            System.out.println("유저 삭제에 실패하였습니다.");
        }
    }
}