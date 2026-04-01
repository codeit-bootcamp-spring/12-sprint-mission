package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User create(User user) {
        data.add(user);
        return user;
    }

    @Override
    public User read(UUID id, User user) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
        }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User update(UUID id, User updatedUser) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
                data.set(i, updatedUser);
                return updatedUser;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
        }

    }








