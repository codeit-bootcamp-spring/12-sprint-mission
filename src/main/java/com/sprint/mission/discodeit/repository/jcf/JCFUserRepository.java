package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

public class JCFUserRepository implements UserRepository {
	private final Map<UUID, User> data;

	public JCFUserRepository() {
		data = new HashMap<>();
	}

	@Override
	public User save(User user) {
		data.put(user.getId(), user);
		return user;
	}

	@Override
	public Optional<User> findById(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<User> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public Long count() {
		return (long)data.size();
	}

	@Override
	public void delete(UUID id) {
		data.remove(id);
	}

	@Override
	public boolean existsById(UUID id) {
		return data.containsKey(id);
	}
}
