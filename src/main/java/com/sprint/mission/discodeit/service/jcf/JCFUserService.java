package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        if (user == null) {
            return null;
        }

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String name, String email, String password, String nickname) {
        User user = findById(id);
        if (user != null) {
            user.update(name, email, password, nickname);
            return user;
        }
        return null;
    }

    public User delete(UUID id) {
        return data.remove(id);
    }
}
