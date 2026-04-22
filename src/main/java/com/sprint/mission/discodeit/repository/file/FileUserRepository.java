package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "file")
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    public FileUserRepository(@Value("${storage.location}") String storageLocation) {
        DIRECTORY = Path.of(storageLocation, "Users");
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
    public User save(User user) {
        if (user == null) throw new NoSuchElementException("채널 객체를 찾을 수 없습니다.");
        if (user.getId() == null) throw new IllegalArgumentException("User ID를 찾을 수 없습니다.");

        Path path = makePath(user.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            return user;
        } catch (IOException e) {
            return null;
        }
    }

    public User loadUser(Path path) {
        if (Files.notExists(path) || Files.isDirectory(path)) return null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object obj = ois.readObject();
            if (!(obj instanceof User)) {
                throw new IllegalArgumentException("파일 내용이 User가 아닙니다.: " + path);
            }
            return (User) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("파일 로드 실패 : " + path);
            return null;
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(loadUser(makePath(id)));
    }

    @Override
    public List<User> findAll() {
        if (!Files.isDirectory(DIRECTORY)) return Collections.emptyList();
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadUser)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(User::getNickname))
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return findAll().stream()
                .anyMatch(user -> user.getNickname().equals(nickname));
    }

    @Override
    public boolean existsById(UUID id) {
        return loadUser(makePath(id)) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public Optional<User> findByNameAndPassword(String name, String password) {
        return findAll().stream()
                .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        try {
            boolean deleted = Files.deleteIfExists(makePath(id));
            if (!deleted){
                System.out.println("삭제 실패 : 해당 ID의 user 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }
}
