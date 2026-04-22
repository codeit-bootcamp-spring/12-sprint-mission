package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponse createPublicChannel(ChannelCreateRequest request);
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelResponse findById(UUID id);
    List<ChannelResponse> findAllByUserId(UUID userId);
    List<ChannelResponse> findAll();
    ChannelResponse update(ChannelUpdateRequest channel);
    void delete(UUID id);
}
