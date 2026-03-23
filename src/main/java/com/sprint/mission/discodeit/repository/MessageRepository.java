package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);
    Message findMessageByContent(String content);
    List<Message> findAllMessage();
    Message changeMessageContent(UUID id, String content);
    Message deleteMessage(UUID id);
}