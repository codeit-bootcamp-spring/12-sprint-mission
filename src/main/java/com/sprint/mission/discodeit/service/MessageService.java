package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public interface MessageService {
    Message create(String content, UUID channelId, UUID authorId);
    Message find(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String newContent);
    void delete(UUID messageId);
}
