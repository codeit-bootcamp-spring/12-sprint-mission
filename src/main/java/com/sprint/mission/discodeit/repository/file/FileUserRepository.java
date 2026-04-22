package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Repository("fileUserRepository")
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository(){
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "users");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id){
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    @Override
    public User save(User user) {
        Path path = makePath(user.getId());
        boolean result = FileUtils.saveObject(path, user);
        if(!result){
            throw new IllegalStateException("유저 저장에 실패했습니다.");
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable((User)FileUtils.loadObject(makePath(id)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return FileUtils.load(DIRECTORY);
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = makePath(id);
        return Files.exists(path);
    }
    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void deleteById(UUID id) {
        Path path = makePath(id);
        if(!Files.exists(path)){
            throw new RuntimeException("삭제 실패: 해당 " + id + "의 파일을 찾을 수 없습니다.");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }
}
