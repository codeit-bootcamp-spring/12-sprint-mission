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
    public User create(String username, String email, String password) {
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
        validateDuplicatedEmail(email);

        User user = new User(username, email, password);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);

        User user = findById(id);
        if (!user.getEmail().equals(email)) {
            validateDuplicatedEmail(email);
        }
        user.update(username, email, password);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        userRepository.delete(id);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("유저 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
    }

    private void validateDuplicatedEmail(String email) {
        boolean exists = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (exists) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }
}
