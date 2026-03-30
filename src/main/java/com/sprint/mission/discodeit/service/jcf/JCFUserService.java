package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final UserRepository userRepository;

    // 직접 받아와서 구성(의존성 주입)
    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);
        userRepository.save(user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID id, String username, String email, String password, String nickname) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.update(username, email, password, nickname);
            userRepository.update(user);
        }
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }
}