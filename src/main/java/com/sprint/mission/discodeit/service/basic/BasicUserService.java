package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {

    // 필요한 Repository 인터페이스를 필드로 선언
    private final UserRepository userRepository;

    // 생성자를 통해 초기화
    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String email, String password, String nickname) {
        // 비즈니스 로직
        User user = new User(username, email, password, nickname);

        // 저장 로직은 Repository 인터페이스 필드를 활용 (직접 구현 X)
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
            // 비즈니스 로직
            user.update(username, email, password, nickname);

            // 저장 로직
            userRepository.update(user);
        }
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }
}