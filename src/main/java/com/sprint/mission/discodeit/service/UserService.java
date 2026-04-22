package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;

public interface UserService {
	User create(UserCreateRequest dto);

	UserDto find(UUID userId);

	List<UserDto> findAll();

	User update(UUID userId, UserUpdateRequestDto requestDto);

	void delete(UUID userId);
}
