package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;

public interface UserStatusService {
	UserStatus create(UserStatusCreateRequestDto dto);

	UserStatus find(UUID id);

	List<UserStatus> findAll();

	UserStatus update(UUID id, UserStatusUpdateRequestDto dto);

	UserStatus updateByUserId(UUID userId);

	void delete(UUID id);
}
