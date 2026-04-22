package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService /*implements UserService*/ {
    private final List<User> data;


    public JCFUserService() {
        data = new ArrayList<>();
    }

    //@Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        data.add(user);
        return user;
    }

    //@Override
    public User find(UUID id) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        throw new RuntimeException("findById 해당 id가 없습니다.");
    }

    //@Override
    public List<User> findAll() {
        return data;
    }

    //@Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = find(userId);
        user.update(newUsername, newEmail, newPassword);
        return user;
    }

    //@Override
    public void delete(UUID id) {
        User user = find(id);
        data.removeIf(u -> u.getId().equals(id));
    }
}
