package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
//        if (user == null) {
//            return null;
//        }

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String name, String email, String password, String nickname) {
        User user = findById(id).orElse(null);

        if (user != null) {
            user.update(name, email, password, nickname);
        }

        return user;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
