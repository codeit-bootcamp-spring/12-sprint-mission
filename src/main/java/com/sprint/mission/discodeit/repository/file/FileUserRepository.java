package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FileSerialization;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;


public class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = "user.ser";

    private final Map<UUID, User> data;

    public FileUserRepository() {
        this.data = new HashMap<>();

        List<User> UserList = FileSerialization.<User>loadData(FILE_PATH);
        for (User user : UserList) {
            data.put(user.getId(), user);
        }
    }

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
        FileSerialization.<User>saveData(FILE_PATH, data.values().stream().toList());
    }

    @Override
    public User findUserByNickname(String nickname) {
        for (User user : data.values()) {
            if (user.getNickname().equals(nickname)) {
                return user;
            }
        }

        throw new IllegalArgumentException("유저 없음.");
    }

    @Override
    public User findUserByEmail(String email) {
        for (User user : data.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User changeUserNickname(UUID id, String nickname) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음.");
        }

        user.update(user.getUsername(), user.getPassword(), user.getEmail(), nickname);
        FileSerialization.<User>saveData(FILE_PATH, data.values().stream().toList());

        return user;
    }

    @Override
    public User deleteUser(UUID id) {
        User user = data.remove(id);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음.");
        }

        FileSerialization.<User>saveData(FILE_PATH, data.values().stream().toList());

        return user;
    }
}
