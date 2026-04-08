package com.sprint.mission.discodeit.service.basic;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class BasicUserService implements UserService {
	private final UserRepository ur;

	public BasicUserService(UserRepository ur) {
		this.ur = ur;
	}

	@Override
	public User create(User user) {
		if (!ur.existsById(user.getId())) {
			ur.save(user);
			return user;
		}
		return null;
	}

	@Override
	public User find(UUID id) {
		return ur.findById(id).orElse(null);
	}

	@Override
	public Collection<User> findAll() {
		return ur.findAll();
	}

	@Override
	public User update(UUID id, String username, String email, String password, String nickname, String phoneNumber,
		String icon) {
		Optional<User> user = ur.findById(id);
		if (user.isPresent() && user.get().getUsername().equals(username)) {
			user.get().update(email, password, nickname, phoneNumber, icon);
			ur.save(user.get());
			return user.get();
		}
		return null;
	}

	@Override
	public void delete(UUID id) {
		Optional<User> user = ur.findById(id);
		if (user.isPresent()) {
			ur.delete(id);
		}
	}
}