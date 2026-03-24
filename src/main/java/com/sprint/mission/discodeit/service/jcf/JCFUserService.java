package com.sprint.mission.discodeit.service.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {

	private final Map<UUID, User> data;

	public JCFUserService() {
		data = new HashMap<>();
	}

	@Override
	public User create(User user) {
		if (!data.containsKey(user.getId())) {
			data.put(user.getId(), user);
			return user;
		} else {
			System.err.println("이미 존재하는 사용자입니다.");
			return null;
		}
	}

	@Override
	public User find(UUID id) {
		return data.get(id);
	}

	@Override
	public List<User> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public User update(UUID id, String username, String email, String password, String nickname, String phoneNumber,
		String icon) {
		for (User updateUser : data.values()) {
			if (updateUser.getId().equals(id)) {
				updateUser.update(username, email, password, nickname, phoneNumber, icon);
				return updateUser;
			}
		}
		return null;
	}

	@Override
	public void delete(UUID id) {
		User user = find(id);
		if (user != null) {
			data.remove(user.getId());
		}
	}
}