package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.data.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.data.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.data.dto.ChannelDto;
import com.sprint.mission.discodeit.data.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.channel.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(PublicChannelCreateRequest request);
    Channel create(PrivateChannelCreateRequest request);

    ChannelDto find(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);

    Channel update(UUID id, ChannelUpdateRequest channelUpdateRequest);

    Channel delete(UUID id);
}
