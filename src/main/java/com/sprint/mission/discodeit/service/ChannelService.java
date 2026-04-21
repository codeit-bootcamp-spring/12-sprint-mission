package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateDTO;
import com.sprint.mission.discodeit.dto.ChannelFindDTO;
import com.sprint.mission.discodeit.dto.ChannelUpdateDTO;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(ChannelCreateDTO channelCreateDTO);
    Channel createPrivate(ChannelCreateDTO channelCreateDTO);
    ChannelFindDTO find(UUID channelId);
    List<ChannelFindDTO> findAllByUserId(UUID userId);
    Channel update(ChannelUpdateDTO channelUpdateDTO);
    void delete(UUID channelId);
}
