package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Service("userStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
	private final UserStatusRepository userStatusRepository;
	private final UserRepository userRepository;
	@Override
	public UserStatus create(UserStatusCreateRequestDto dto) {
		if (userRepository.existsById(dto.userId())) {
			throw new IllegalArgumentException("User not found with id " + dto.userId());
		}
		if (userStatusRepository.existsById(dto.userId())) {
			throw new IllegalArgumentException("UserStatus already exists for user with id " + dto.userId());
		}
		return userStatusRepository.save(UserStatus.builder()
				.userId(dto.userId())
				.lastLogin(dto.lastOnlineTime())
				.build());
	}

	@Override
	public UserStatus find(UUID id) {
		return userStatusRepository.findById(id).orElse(null);
	}

	@Override
	public List<UserStatus> findAll() {
		return userStatusRepository.findAll();
	}

	@Override
	public UserStatus update(UUID id, UserStatusUpdateRequestDto dto) {
		UserStatus userStatus = find(id);
		userStatus.update(dto.lastOnlineTime());
		return userStatusRepository.save(userStatus);
	}

	@Override
	public UserStatus updateByUserId(UUID userId) {
		UserStatus userStatus = findAll().stream()
			.filter(u -> u.getUserId().equals(userId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("UserStatus not found with userId " + userId));
		userStatus.update(Instant.now());
		return userStatusRepository.save(userStatus);
	}

	@Override
	public void delete(UUID id) {
		if (!userStatusRepository.existsById(id)) {
			throw new IllegalArgumentException("UserStatus not found with id " + id);
		}
		userStatusRepository.deleteById(id);
	}
}
