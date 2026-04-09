package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelCategory;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelCategory category, String name, String description);
    Channel read(UUID ChannelId);
    List<Channel> readAll();
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
}
