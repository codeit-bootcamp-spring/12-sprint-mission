package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // PUBLIC 채널 생성
    ChannelDto create(PublicChannelCreateRequest request);

    // PRIVATE 채널 생성
    ChannelDto create(PrivateChannelCreateRequest request);

    ChannelDto findById(UUID id);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto update(ChannelUpdateRequest request);

    ChannelDto delete(UUID id);
}
