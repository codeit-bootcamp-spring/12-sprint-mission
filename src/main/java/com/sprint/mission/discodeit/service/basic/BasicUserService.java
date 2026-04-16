package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@Service("userService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {
	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final UserStatusRepository userStatusRepository;

	@Override
	public User create(UserCreateRequest dto) {

		if (dto.username() == null) {
			throw new IllegalArgumentException("Username cannot be null");
		} else if (dto.email() == null) {
			throw new IllegalArgumentException("Email cannot be null");
		} else if (dto.password() == null) {
			throw new IllegalArgumentException("Password cannot be null");
		}
		if (userRepository.findByUsername(dto.username()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}
		if (userRepository.findByEmail(dto.email()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}
		UUID profileId = Optional.ofNullable(dto.profileImage())
			.map(p -> BinaryContent.builder().content(p).build())
			.map(binaryContentRepository::save)
			.map(BinaryContent::getId)
			.orElse(null);
		User user = User.builder()
			.username(dto.username())
			.email(dto.email())
			.password(dto.password())
			.profileId(profileId)
			.build();
		userStatusRepository.save(UserStatus.builder().userId(user.getId()).build());
		return userRepository.save(user);
	}

	@Override
	public UserDto find(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
		boolean status = userStatusRepository.findByUserId(userId)
			.map(UserStatus::isOnline)
			.orElse(false);
		return UserDto.from(user, status);
	}

	@Override
	public List<UserDto> findAll() {
		return userRepository.findAll().stream()
			.map(user -> {
				boolean status = userStatusRepository.findByUserId(user.getId())
					.map(UserStatus::isOnline)
					.orElse(false);
				return UserDto.from(user, status);
			})
			.toList();
	}

	@Override
	public User update(UUID userId, UserUpdateRequestDto requestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
		UUID newProfileId = null;
		if (requestDto.newProfileImage() != null) {
			newProfileId = BinaryContent.builder().content(requestDto.newProfileImage()).build().getId();
		}
		user.update(requestDto.newUsername(), requestDto.newEmail(), requestDto.newPassword(), newProfileId);
		return userRepository.save(user);
	}

	@Override
	public void delete(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
		if (user.getProfileId() != null) {
			binaryContentRepository.deleteById(user.getProfileId());
		}
		userStatusRepository.findByUserId(user.getId())
			.ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));
		userRepository.deleteById(userId);
	}
}
