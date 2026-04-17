package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.data.message.MessageResponse;
import com.sprint.mission.discodeit.dto.data.message.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponse create(MessageCreateRequest request);

    MessageResponse find(UUID id);

    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse update(UUID id, MessageUpdateRequest request);

    void delete(UUID id);

}