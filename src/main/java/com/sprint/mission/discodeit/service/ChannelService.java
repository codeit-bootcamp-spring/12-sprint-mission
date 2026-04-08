package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel save(Channel channel);

    Channel findById(UUID channelId);

    List<Channel> findAll();

    void update(UUID channelId, String title);

    void delete(UUID channelId);
}