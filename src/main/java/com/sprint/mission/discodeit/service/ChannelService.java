package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(CreatePublicChannelRequest request);
    Channel createPrivate(CreatePrivateChannelRequest request);
    ChannelDto find(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    Channel update(UpdateChannelRequest request);
    void delete(UUID channelId);
}
