package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto create(ChannelDto channelDto);

  ChannelDto find(UUID id);

  Channel update(UUID id, ChannelDto channelDto);

  ChannelDto delete(UUID id);

  List<ChannelDto> findChannelsByUser(UUID userId);
}