package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.CreateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.EmailVerifier;

import java.util.*;

public class BasicUserService implements UserService {
    private UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(CreateUserRequest request) {
        User user = new User(request.username(), request.password(), request.email(), request.nickname());

        EmailVerifier.isValidEmail(request.email());
        if (userRepository.findUserByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일.");
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserByNickname(String nickname) {
        return userRepository.findUserByNickname(nickname);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public User changeUserNickname(UUID id, String nickname) {
        return userRepository.changeUserNickname(id, nickname);
    }

    @Override
    public User deleteUser(UUID id) {
        return userRepository.deleteUser(id);
    }
}