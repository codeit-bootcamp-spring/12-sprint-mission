package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(CreatePublicChannelRequest dto);
    ChannelResponse createPrivateChannel(CreatePrivateChannelRequest dto);
    ChannelResponse findById(UUID channelId);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(UUID channelId , ChannelUpdateRequest dto);
    void delete(UUID channelId);
}
