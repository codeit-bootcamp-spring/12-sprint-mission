package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateDTO messageCreateDTO);
    Message find(UUID messageId);
    List<Message> findByChannelId(UUID channelId);
    Message update(MessageUpdateDTO messageUpdateDTO);
    void delete(UUID messageId);
}
