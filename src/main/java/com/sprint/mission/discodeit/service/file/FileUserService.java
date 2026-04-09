package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// User : Serializable O
// Channel : Serializable O
// Message : Serializable O

//[O] CRUD 기능 구현 (테스트 X)
//[O] 폴더 경로 구현

public class FileUserService implements UserService {
    private final Path USER_PATH = Path.of("data","users");

    public FileUserService() {
        FileUtils.createDirectories(USER_PATH);
    }

    @Override
    public User save(User user) {
        Path path = USER_PATH.resolve(user.getId().toString() + ".ser");
        FileUtils.saveObject(path, user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        Path path = USER_PATH.resolve(id.toString() + ".ser");
        return  (User) FileUtils.loadObject(path);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        if(!Files.exists(USER_PATH)){
            return users;
        }
        try {
            Files.list(USER_PATH).forEach(file -> {
                users.add((User) FileUtils.loadObject(file));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public User update(User user) {
        Path path = USER_PATH.resolve(user.getId().toString() + ".ser");
        if(!Files.exists(path)){
            throw new RuntimeException("User not found");
        }
        FileUtils.saveObject(path, user);
        return user;
    }

    @Override
    public User delete(UUID id) {
        User user = findById(id);
        Path path = USER_PATH.resolve(user.getId().toString() + ".ser");
        try {
            Files.delete(path);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user");
        }
    }
}
