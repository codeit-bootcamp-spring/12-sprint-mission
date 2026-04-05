package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message send(Message message);
    Message getMessage(UUID id);
    List<Message> findAllByChannelId(UUID channelId);
    Message edit(UUID id, String content);
    void remove(UUID id);
}
