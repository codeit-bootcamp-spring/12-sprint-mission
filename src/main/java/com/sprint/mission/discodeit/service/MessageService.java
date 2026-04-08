package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    /// Create
    UUID create(Message message);

    /// Read
    Optional<Message> findById(UUID id);
    Optional<List<Message>> findBySendUser(User user);
//    List<Message> findByChannelId(UUID id);
    Optional<List<Message>> findAll();

    /// Update
    void updateById(UUID id, String content);

    /// Delete
    void deleteById(UUID id);
}
