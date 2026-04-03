package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    void delete(UUID id);
}
