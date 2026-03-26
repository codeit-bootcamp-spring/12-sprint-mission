package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService(Map<UUID, User> data) {
        this.data = data;
    }

    @Override
    public User create(User user) {
        return user;
    }

    @Override
    public User read(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, User updatedUser) {
       data.put(id, updatedUser);
       return updatedUser;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}

