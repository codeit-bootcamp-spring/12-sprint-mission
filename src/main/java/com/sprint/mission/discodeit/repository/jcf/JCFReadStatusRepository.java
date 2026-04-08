package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
	private final Map<UUID, ReadStatus> data;

	public JCFReadStatusRepository() {
		this.data = new HashMap<>();
	}

	@Override
	public ReadStatus save(ReadStatus readStatus) {
		data.put(readStatus.getId(), readStatus);
		return data.get(readStatus.getId());
	}

	@Override
	public Optional<ReadStatus> findById(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<ReadStatus> findAll() {
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
