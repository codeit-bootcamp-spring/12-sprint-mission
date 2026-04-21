package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateDTO;
import com.sprint.mission.discodeit.dto.UserFindDTO;
import com.sprint.mission.discodeit.dto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateDTO userCreateDTO);
    UserFindDTO find(UUID userId);
    List<UserFindDTO> findAll();
    User update(UserUpdateDTO userUpdateDTO);
    void delete(UUID userId);
}
