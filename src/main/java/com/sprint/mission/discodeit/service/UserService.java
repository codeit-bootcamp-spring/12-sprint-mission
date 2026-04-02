package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String password, String nickname);
    Optional<User> findById(UUID id);
    List<User> findAll();
    void update(UUID id, String name, String email, String password, String nickname);
    void delete(UUID id);
}