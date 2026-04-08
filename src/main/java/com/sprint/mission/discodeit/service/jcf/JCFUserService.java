package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final UserRepository userRepo = new JCFUserRepository();


    @Override
    public User create(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("아이디에 공백을 입력할 수 없습니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일에 공백을 입력할 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 공백을 입력할 수 없습니다.");
        }

        User user = new User(username, email, password);
        return userRepo.save(user);
    }

    @Override
    public User findById(UUID id) {
        User user = userRepo.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepo.findAll();
        users.sort((u1, u2) -> Long.compare(u1.getCreatedAt(), u2.getCreatedAt()));
        return users;
    }

    @Override
    public User updateUsername(UUID id, String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("아이디에 공백을 입력할 수 없습니다.");
        }
        User user = findById(id);
        user.updateUsername(username);
        return userRepo.save(user);
    }

    @Override
    public User updateEmail(UUID id, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일에 공백을 입력할 수 없습니다.");
        }
        User user = findById(id);
        user.updateEmail(email);
        return userRepo.save(user);
    }

    @Override
    public User updatePassword(UUID id, String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 공백을 입력할 수 없습니다.");
        }
        User user = findById(id);
        user.updatePassword(password);
        return userRepo.save(user);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        userRepo.deleteById(id);
    }

}