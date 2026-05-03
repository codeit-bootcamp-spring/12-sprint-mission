package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageReceiveInfoDto;

import java.util.List;
import java.util.UUID;

public interface MessageReceiveInfoService {

  MessageReceiveInfoDto create(MessageReceiveInfoDto dto);

  MessageReceiveInfoDto find(UUID id);

  MessageReceiveInfoDto update(UUID id, MessageReceiveInfoDto dto);

  MessageReceiveInfoDto delete(UUID id);

  List<MessageReceiveInfoDto> findByUserId(UUID userId);
}
