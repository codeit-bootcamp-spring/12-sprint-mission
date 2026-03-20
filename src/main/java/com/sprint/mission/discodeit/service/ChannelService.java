package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    User save (User user);
    User findById(UUID id);
    List<User> findAll();
    User update(UUID id, String name, String email, String password, String nickname);
    User delete(UUID id);
}
