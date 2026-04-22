package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FileSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = "User.ser";

    private final Map<UUID, User> data;

    public FileUserRepository() {
        this.data = new HashMap<>();

        List<User> UserList = FileSerialization.loadData(FILE_PATH);
        for (User user : UserList) {
            data.put(user.getId(), user);
        }
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        for (User user : data.values()) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : data.values()) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User deleteById(UUID id) {
        User removed = data.remove(id);

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 User 없음.");
        }

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return removed;
    }
}
