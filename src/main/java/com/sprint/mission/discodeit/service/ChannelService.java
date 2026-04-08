package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelCreatePrivateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelCreatePublicRequestDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelCreatePrivateRequestDto dto);
    Channel create(ChannelCreatePublicRequestDto dto);
    ChannelDto find(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    Channel update(UUID channelId, ChannelUpdateRequestDto dto);
    void delete(UUID channelId);
}
