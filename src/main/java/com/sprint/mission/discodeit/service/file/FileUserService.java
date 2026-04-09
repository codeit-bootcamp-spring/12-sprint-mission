package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService(){
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "users");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id){
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        Path path = makePath(user.getId());
        boolean result = FileUtils.saveObject(path, user);
        if(!result){
            throw new IllegalStateException("유저 저장에 실패했습니다.");
        }
        return user;
    }

    @Override
    public User find(UUID userId) {
        return (User)FileUtils.loadObject(makePath(userId));
    }

    @Override
    public List<User> findAll() {
        return FileUtils.load(DIRECTORY);
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        return null;
    }

    @Override
    public void delete(UUID id) {
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

    public void deleteAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)) {
            stream.filter(path ->  path.getFileName().toString().endsWith(EXTENSION))
                    .forEach(path ->{
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
