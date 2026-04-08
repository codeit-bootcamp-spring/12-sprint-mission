package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

@Repository
public class JCFUserRepository implements UserRepository {
	private final Map<UUID, User> data;

	public JCFUserRepository() {
		this.data = new HashMap<>();
	}

	@Override
	public User save(User user) {
		this.data.put(user.getId(), user);
		return user;
	}

	@Override
	public Optional<User> findById(UUID id) {
		return Optional.ofNullable(this.data.get(id));
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return Optional.empty();
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return Optional.empty();
	}

	@Override
	public List<User> findAll() {
		return this.data.values().stream().toList();
	}

	@Override
	public boolean existsById(UUID id) {
		return this.data.containsKey(id);
	}

	@Override
	public void deleteById(UUID id) {
		this.data.remove(id);
	}
}
