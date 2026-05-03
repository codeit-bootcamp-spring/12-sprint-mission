package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MesaageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;

  public MesaageServiceImpl(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Override
  public MessageDto send(MessageDto messageDto) {
    return null;
  }

  @Override
  public MessageDto find(UUID id) {
    return null;
  }

  @Override
  public MessageDto update(UUID id, MessageDto messageDto) {
    return null;
  }

  @Override
  public MessageDto delete(UUID id) {
    return null;
  }

  @Override
  public List<MessageDto> findByChannelId(UUID channelId) {
    return List.of();
  }
}
