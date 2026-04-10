package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    User findById(UUID id);

    List<User> findAll();

    boolean existsByNickname(String nickname);

    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    Optional<User> findByNameAndPassword(String name, String password);

    void deleteById(UUID id);
}
