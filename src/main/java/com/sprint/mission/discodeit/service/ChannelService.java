package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelCategory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service


public interface ChannelService {
    default Channel create(String name, String description, ChannelCategory category) {
        return null;
    }

    Channel create(ChannelCategory type, String name, String description);

    Channel find(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
}
