package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public void update(UUID userId, String username, String email, String password, String nickname) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                user.update(username, email, password, nickname);
                break;
            }
        }
    }

    @Override
    public void delete(UUID userId) {
        data.removeIf(user -> user.getId().equals(userId));
    }
}