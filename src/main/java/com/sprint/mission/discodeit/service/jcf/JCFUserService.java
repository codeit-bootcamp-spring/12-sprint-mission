package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {

	private final List<User> data;

	public JCFUserService() {
		data = new ArrayList<>();
	}

	@Override
	public User create(User user) {
		data.add(user);
		return user;
	}

	@Override
	public User find(UUID id) {
		return data.stream().filter(idata -> idata.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public List<User> findAll() {
		return data;
	}

	@Override
	public User update(UUID id, String username, String email, String password, String nickname, String phoneNumber,
		String icon) {
		for (User updateUser : data) {
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
			data.remove(user);
		}
	}
}