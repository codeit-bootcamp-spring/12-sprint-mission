package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User save(User user);

    User findById(UUID userId);

    List<User> findAll();

    void update(UUID userId, String username, String email, String password, String nickname);

    void delete(UUID userId);
}