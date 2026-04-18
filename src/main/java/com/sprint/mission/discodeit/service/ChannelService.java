package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelCategory;


import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublic(PublicChannelCreateRequest request);
    ChannelDto createPrivate(PrivateChannelCreateRequest request);
    ChannelDto find(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto update(UUID channelId, ChannelUpdateRequest request);
    void delete(UUID channelId);

    Channel create(String general, String 공지, ChannelCategory channelCategory);
}
