package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel save (Channel channel);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String name, String description);
    Channel delete(UUID id);
}
