package com.sprint.mission.discodeit.service;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {

	@InjectMocks
	private BasicUserService userService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private UserStatusRepository userStatusRepository;
	@Mock
	private UserMapper userMapper;
	@Mock
	private BinaryContentRepository binaryContentRepository;
	@Mock
	private BinaryContentStorage binaryContentStorage;

	private UserCreateRequest createUserCreateRequest() {
		return new UserCreateRequest("testuser", "test@example.com", "password123");
	}

	private UserUpdateRequest createUserUpdateRequest() {
		return new UserUpdateRequest("newuser", "new@example.com", "newpassword123");
	}

	private BinaryContentCreateRequest createProfileRequest() {
		return new BinaryContentCreateRequest("profile.png", "image/png", new byte[]{1, 2, 3});
	}

	@Test
	@DisplayName("Create - 성공: 프로필 이미지가 없는 사용자 생성")
	void create_Success_WithoutProfile() {
		// given
		UserCreateRequest request = createUserCreateRequest();

		given(userRepository.existsByEmail(request.email())).willReturn(false);
		given(userRepository.existsByUsername(request.username())).willReturn(false);
		given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(userMapper.toDto(any(User.class))).willReturn(new UserDto(UUID.randomUUID(), request.username(), request.email(), null, true));

		// when
		UserDto result = userService.create(request, Optional.empty());

		// then
		assertThat(result).isNotNull();
		then(binaryContentRepository).should(never()).save(any());
		then(binaryContentStorage).should(never()).put(any(), any());
		then(userRepository).should(times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("Create - 실패: 이메일이 이미 존재하면 예외가 발생한다")
	void create_ThrowsException_WhenEmailExists() {
		// given
		UserCreateRequest request = createUserCreateRequest();
		given(userRepository.existsByEmail(request.email())).willReturn(true);

		// when & then
		assertThatThrownBy(() -> userService.create(request, Optional.empty()))
			.isInstanceOf(UserAlreadyExistException.class);

		then(userRepository).should(never()).save(any());
	}


	@Test
	@DisplayName("Update - 성공: 프로필 변경을 포함한 사용자 정보 수정")
	void update_Success_WithNewProfile() {
		// given
		UUID userId = UUID.randomUUID();
		UserUpdateRequest updateRequest = createUserUpdateRequest();
		BinaryContentCreateRequest profileRequest = createProfileRequest();
		User existingUser = new User("oldUser", "old@example.com", "oldPassword", null);

		given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
		given(userRepository.existsByEmail(updateRequest.newEmail())).willReturn(false);
		given(userRepository.existsByUsername(updateRequest.newUsername())).willReturn(false);
		given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(userMapper.toDto(any(User.class))).willReturn(new UserDto(userId, updateRequest.newUsername(), updateRequest.newEmail(), null, null));

		// when
		UserDto result = userService.update(userId, updateRequest, Optional.of(profileRequest));

		// then
		assertThat(result).isNotNull();
		assertThat(result.username()).isEqualTo(updateRequest.newUsername());

		then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
		then(binaryContentStorage).should(times(1)).put(any(), any(byte[].class));
	}

	@Test
	@DisplayName("Update - 실패: 수정하려는 이메일이 이미 존재하면 예외가 발생한다")
	void update_ThrowsException_WhenNewEmailExists() {
		// given
		UUID userId = UUID.randomUUID();
		UserUpdateRequest updateRequest = createUserUpdateRequest();
		User existingUser = new User("oldUser", "old@example.com", "oldPassword", null);

		given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
		given(userRepository.existsByEmail(updateRequest.newEmail())).willReturn(true);

		// when & then
		assertThatThrownBy(() -> userService.update(userId, updateRequest, Optional.empty()))
			.isInstanceOf(UserAlreadyExistException.class);
	}

	@Test
	@DisplayName("Delete - 성공: 존재하는 사용자를 삭제한다")
	void delete_Success() {
		// given
		UUID userId = UUID.randomUUID();
		given(userRepository.existsById(userId)).willReturn(false);

		// when
		userService.delete(userId);

		// then
		then(userRepository).should(times(1)).deleteById(userId);
	}

	@Test
	@DisplayName("Delete - 실패: 존재하지 않는 사용자 삭제 시 예외가 발생한다")
	void delete_ThrowsException_WhenUserNotFound() {
		// given
		UUID userId = UUID.randomUUID();
		given(userRepository.existsById(userId)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> userService.delete(userId))
			.isInstanceOf(UserNotFoundException.class);

		then(userRepository).should(never()).deleteById(any());
	}
}