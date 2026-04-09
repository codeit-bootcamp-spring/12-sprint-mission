package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);
        return userRepository.save(user);
    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId).
                orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userRepository.findAll());
    }

   @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword, newNickname);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userid) {
        if (!userRepository.existsById(userid)) {
            throw new NoSuchElementException("User with id " + userid + " not found");
        }
        userRepository.delete(userid);
    }
}



