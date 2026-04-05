package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    Optional<Message> update(UUID id, String content, UUID userId, UUID channelId);
    boolean deleteById(UUID id);
}
