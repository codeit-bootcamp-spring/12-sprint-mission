package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponse createPublic(ChannelPublicCreateRequest channel);

    ChannelResponse createPrivate(ChannelPrivateCreateRequest request);

    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse update(UUID channelId, ChannelUpdateRequest request);

    void delete(UUID id);

}
