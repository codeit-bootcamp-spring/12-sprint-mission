package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    /// CREATE & UPDATE
    void save(Message message);

    /// READ
    Optional<Message> findById(UUID id);
    Optional<List<Message>> findAll();

    /// DELETE
    void deleteById(UUID id);
}
