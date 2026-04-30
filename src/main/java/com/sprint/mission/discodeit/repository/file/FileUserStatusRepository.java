package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {
    private final File file = new File("userStatus.dat");

    private List<UserStatus> readAll() {
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<UserStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeAll(List<UserStatus> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        List<UserStatus> list = readAll();

        // 기존 데이터 있으면 교체
        list.removeIf(s -> s.getId().equals(userStatus.getId()));
        list.add(userStatus);

        writeAll(list);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return readAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return readAll().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return readAll();
    }

    @Override
    public boolean existsById(UUID id) {
        return readAll().stream()
                .anyMatch(s -> s.getId().equals(id));
    }

    @Override
    public void deleteById(UUID id) {
        List<UserStatus> list = readAll();
        list.removeIf(s -> s.getId().equals(id));
        writeAll(list);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<UserStatus> list = readAll();
        list.removeIf(s -> s.getUserId().equals(userId));
        writeAll(list);
    }


}
