package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> findUser = new ArrayList<>();
        for (User user : users.values()) {
            findUser.add(user);
        }
        return findUser;
//        return users.values().stream().toList();
    }

    @Override
    public User update(User user) {
        if(users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return users.get(user.getId());
        }
        return null;
    }

    @Override
    public User delete(UUID id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }
}
