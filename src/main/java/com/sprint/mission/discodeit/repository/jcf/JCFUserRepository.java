package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;


public class JCFUserRepository implements UserRepository {
    private static final String FILE_PATH = "user.ser";

    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        for (User user : data.values()) {
            if (user.getNickname().equals(nickname)) {
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
    public User delete(UUID id) {
        User user = data.remove(id);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음.");
        }

        return user;
    }
}
