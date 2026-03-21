package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {
	User create(User user);

	User find(UUID id);

	List<User> findAll();

	User update(UUID id, String username, String email, String password, String nickname, String phoneNumber,
		String icon);

	void delete(UUID id);

}