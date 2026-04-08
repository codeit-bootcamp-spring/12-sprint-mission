package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
	private final Map<UUID, UserStatus> data;

	public JCFUserStatusRepository() {
		this.data = new HashMap<>();
	}

	@Override
	public UserStatus save(UserStatus userStatus) {
		data.put(userStatus.getId(), userStatus);
		return data.get(userStatus.getId());
	}

	@Override
	public Optional<UserStatus> findById(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public Optional<UserStatus> findByUserId(UUID userId) {
		return data.values().stream()
			.filter(u -> u.getUserId().equals(userId))
				.findFirst();
	}

	@Override
	public List<UserStatus> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public boolean existsById(UUID id) {
		return data.containsKey(id);
	}

	@Override
	public void deleteById(UUID id) {
		data.remove(id);
	}
}
