package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String username, String email, int password, String nickname) {
        boolean exists = userRepository.findAll().stream()
                .anyMatch(u -> u.getNickname().equals(nickname));
        if (exists) {
            throw new IllegalArgumentException("nickname already exists!");
        }
        User user = new User(username, email, password, nickname);
        userRepository.save(user);
        return user;
    }

    @Override
    public User find(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    public void delete(UUID id) {
    }
}



