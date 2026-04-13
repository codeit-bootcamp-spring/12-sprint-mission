package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.message.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Optional<Message> findById(UUID id);
    Optional<Message> findByContent(String content);
    List<Message> findAllByChannelId(UUID channelId);
    List<Message> findAll();

    Message deleteById(UUID id);
    List<Message> deleteAllByChannelId(UUID channelId);
}