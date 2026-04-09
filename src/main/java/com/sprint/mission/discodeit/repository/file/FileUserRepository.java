package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@Repository("userRepository")
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"),"my_dir", "users");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }


    @Override
    public User save(User user) {
        Path path = makePath(user.getId());
        boolean result = FileUtils.saveObject(path, user);
        if (!result) {
            throw new IllegalStateException("Could not save user");
        }
        return user;
    }

    @Override
    public User findById(UUID id) {
        return (User) FileUtils.loadObject(makePath(id));
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (User)FileUtils.loadObject(path))
                    .toList();
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public User update(User user) {
        Path path = makePath(user.getId());
        if(!Files.exists(path)) {
            throw new NoSuchElementException("수정할 사용자가 없습니다.");
        }
        return save(user);
    }

    @Override
    public User delete(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            return  null;
        }
        User userDel = (User) FileUtils.loadObject(path);
        try {
            Files.delete(path);
            return userDel;
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제 할 수 없습니다.");
        }
    }
}
