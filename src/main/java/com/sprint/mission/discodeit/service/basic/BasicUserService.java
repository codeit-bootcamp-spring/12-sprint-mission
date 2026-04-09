package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NoSuchElementException("존재하지 않는 사용자: " + userId);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void update(UUID userId, String username, String email, String password, String nickname) {
        User user = findById(userId);
        user.update(username, email, password, nickname);
        userRepository.update(user);
    }

    @Override
    public void delete(UUID userId) {
        userRepository.delete(userId);
    }
}