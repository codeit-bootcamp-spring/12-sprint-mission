package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    /// Create
    UUID create(User user);

    /// Read
    Optional<User> findById(UUID id);
    Optional<List<User>> findAll();

    /// Update
    void updateById(UUID id, String username, String email, String password, String nickname);

    /// Delete
    void deleteById(UUID id);
}
