package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.data.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.data.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.message.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest messageCreateRequest);

    Message find(UUID id);
    List<Message> findAllByChannelId(UUID channelId);

    Message update(UUID id, MessageUpdateRequest request);

    Message delete(UUID id);
}
