package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Message findById(UUID id);

    List<Message> findAll();

    List<Message> findByChannelId(UUID id);

    boolean existsById (UUID id);

    void delete(UUID id);
}
