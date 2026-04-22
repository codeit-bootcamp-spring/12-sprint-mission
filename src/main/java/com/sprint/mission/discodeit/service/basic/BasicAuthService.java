package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.LoginRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service("authService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
	private final UserRepository userRepository;
	@Override
	public User login(LoginRequestDto loginRequestDto) {
		return userRepository.findAll().stream()
			.filter(u -> u.getUsername().equals(loginRequestDto.username()) && u.getPassword().equals(loginRequestDto.username()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
	}
}
