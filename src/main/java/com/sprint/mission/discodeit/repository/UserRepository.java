package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    /// CREATE & UPDATE
    void save(User user);

    /// READ
    Optional<User> findById(UUID id);
    Optional<List<User>> findAll();

    /// DELETE
    void deleteById(UUID id);
}
