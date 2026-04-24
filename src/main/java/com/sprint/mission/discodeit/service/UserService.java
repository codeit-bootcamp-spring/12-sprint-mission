package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface UserService {
    User create(UserDto userDto);

    UserDto find(UUID userId);

    List<UserDto> findAll();

    User update(UUID Id, UserDto userDto);

    UserDto delete(UUID userId);

    User updateOnlineStatus(UUID userId, Boolean online);

    User login(String email, String password);

}