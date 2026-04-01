package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, String sender, String receiver);
    Message read(UUID id, Message message);
    List<Message> readAll();
    Message update(UUID id, String content, String sender, String receiver);
    void delete(String message);
}
