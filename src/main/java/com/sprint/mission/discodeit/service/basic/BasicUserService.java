package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.CreateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.EmailVerifier;

import java.util.*;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(CreateUserRequest request) {
        User user = new User(request.username(), request.password(), request.email(), request.nickname());

        EmailVerifier.isValidEmail(request.email());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일.");
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserByNickname(String nickname) {
        Optional<User> opUser = userRepository.findByNickname(nickname);

        if (opUser.isPresent()) {
            return opUser.get();
        } else {
            throw new IllegalArgumentException("해당 닉네임을 가진 유저 없음");
        }
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User changeUserNickname(UUID id, String nickname) {
        Optional<User> opUser = userRepository.findById(id);

        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("해당 id를 가진 유저 없음.");
        }
        User msg = opUser.get();

        msg.update(msg.getUsername(), msg.getPassword(), msg.getEmail(), nickname);

        return userRepository.save(msg);
    }


    @Override
    public User deleteUser(UUID id) {
        return userRepository.delete(id);
    }
}