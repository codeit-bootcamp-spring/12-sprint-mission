package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, int password, String nickname);

    User find(UUID id);

    List<User> findAll();

    void delete(UUID id);


}




