package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        userMap.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return userMap.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());

    }

    @Override
    public void deleteById(UUID id) {
        userMap.remove(id);
    }
}
