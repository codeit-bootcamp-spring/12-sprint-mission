package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto send(MessageDto messageDto);

  MessageDto find(UUID id);

  MessageDto update(UUID id, MessageDto messageDto);

  MessageDto delete(UUID id);

  List<MessageDto> findByChannelId(UUID channelId);
}
