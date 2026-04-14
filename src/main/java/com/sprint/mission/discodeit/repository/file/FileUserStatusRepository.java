package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "UserStatuses");
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }


    public FileUserStatusRepository() {
        createDirectory(DIRECTORY);
    }

    public void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        if (userStatus.getId() == null) throw new NoSuchElementException("UserStatus 객체를 찾을 수 없습니다.");
        if (userStatus.getUserId() == null) throw new IllegalArgumentException("UserStatus의 유저 정보가 누락되었습니다.");

        Path path = makePath(userStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);
            return userStatus;
        } catch (IOException e) {
            return null;
        }
    }

    public UserStatus loadUserStatus(Path path) {
        if (Files.notExists(path) || Files.isDirectory(path)) return null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object obj = ois.readObject();
            if (!(obj instanceof UserStatus)) {
                throw new IllegalArgumentException("파일 내용이 UserStatus가 아닙니다 : " + path);
            }
            return (UserStatus) obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 입출력 에러");
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(loadUserStatus(makePath(id)));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        if(Files.notExists(DIRECTORY)) return Collections.emptyList();

        try (Stream<Path> stream = Files.list(DIRECTORY)){
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadUserStatus)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public void delete(UUID id) {
        try{
            boolean deleted = Files.deleteIfExists(makePath(id));
            if(!deleted){
                System.out.println("삭제 실패 : 해당 ID의 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }
}
