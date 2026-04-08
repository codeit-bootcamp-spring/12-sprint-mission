package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, UUID authorId, UUID channelId);
    Message findById(UUID id);
    List<Message> findAll();
    Message updateContent(UUID id, String content);
    void deleteById(UUID id);
}
