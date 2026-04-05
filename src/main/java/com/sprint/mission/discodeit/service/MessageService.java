package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, String sender, String receiver);
    Message read(UUID id, Message message);
    List<Message> readAll();
    void delete(String message);
}
