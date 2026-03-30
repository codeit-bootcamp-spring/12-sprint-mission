package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        if (user == null) {
            System.err.println("입력된 사용자 객체가 null입니다.");
            return null;
        }

        if (userRepository.findById(user.getId()).isPresent()) {
            System.err.println("이미 존재하는 ID의 사용자입니다.");
            return null;
        }

        if (userRepository.findAll().stream()
                .anyMatch(u -> u.getNickname().equals(user.getNickname()))) {
            System.err.println("이미 존재하는 닉네임입니다.");
            return null;
        }

        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String name, String email, String password, String nickname) {
        return userRepository.update(id, name, email, password, nickname);
    }

    @Override
    public User delete(UUID id) {
        return userRepository.delete(id);
    }

    public User create(String username, String email, String password, String nickname) {
        if(username == null || email == null || password == null || nickname == null ){
            return null;
        }

        return save(new User(username, email, password, nickname));
    }
}
