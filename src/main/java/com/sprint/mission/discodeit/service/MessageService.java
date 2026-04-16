package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    Message update(UUID id, Message message);
    boolean delete(UUID id);
}
