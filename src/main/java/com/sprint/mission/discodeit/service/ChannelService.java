package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.channel.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.dto.data.channel.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.dto.data.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.data.channel.ChannelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponse createPublic(ChannelCreatePublicRequest request);

    ChannelResponse createPrivate(ChannelCreatePrivateRequest request);

    ChannelResponse find(UUID id);

    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse update(UUID id, ChannelUpdateRequest request);

    void delete(UUID id);

}