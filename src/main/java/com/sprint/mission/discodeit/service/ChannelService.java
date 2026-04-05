package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel save(Channel channel);
    Optional<Channel> findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String name, String description);
    boolean deleteById(UUID id);
}
