package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType channelType, String channelName, String description);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel updateChannelType(UUID id , ChannelType channelType);
    Channel updateChannelName(UUID id , String channelName);
    Channel updateChannelDescription(UUID id, String description);
    void deleteById(UUID id);

}
