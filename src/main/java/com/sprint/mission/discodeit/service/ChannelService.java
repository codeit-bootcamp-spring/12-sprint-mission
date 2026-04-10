package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponse createPublicChannel(ChannelCreateRequest request);
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelResponse findById(UUID id);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(ChannelUpdateRequest channel);
    void delete(UUID id);
}
