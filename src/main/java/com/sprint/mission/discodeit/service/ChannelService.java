package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);
    Optional<Channel> findById(UUID id);
    List<Channel> findAll();
    void update(UUID id, String name);
    void delete(UUID id);
}
