package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;


public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> userData = new HashMap<>();

    @Override
    public void save(User user) {
        userData.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        return userData.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userData.values());
    }

    @Override
    public void update(User user) {
        userData.put(user.getId(), user);
    }

    @Override
    public void delete(UUID id) {
        userData.remove(id);
    }
}