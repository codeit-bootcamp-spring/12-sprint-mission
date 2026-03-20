package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message save (Message message);
    Message findById(UUID messageId);
    List<Message> findAll();
    Message update(UUID messageId, String content, UUID channelId);
    Message delete(UUID messageId);
}
