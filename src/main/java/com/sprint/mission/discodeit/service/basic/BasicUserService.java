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
        if (userRepository.findById(user.getId()) != null)
            throw new IllegalStateException("이미 동일한 id의 user가 존재합니다.");
        validateUser(user);
        if (user.getNickname() == null || user.getNickname().isBlank()) {
            user.update(new User(null, null, user.getName(), null));
        }
        return userRepository.save(user);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank())
            throw new IllegalArgumentException("이름은 필수 입력 항목 입니다.");
        if (user.getName().length() < 2 || user.getName().length() > 10)
            throw new IllegalArgumentException("이름은 2자 이상 10자 이하로 입력해주세요.");

        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new IllegalArgumentException("이메일은 필수 입력 항목 입니다.");
        if (!user.getEmail().contains("@"))
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");

        if (user.getPassword() == null || user.getPassword().isBlank())
            throw new IllegalArgumentException("비밀번호는 필수 입력 항목 입니다.");
        if (user.getPassword().length() < 8)
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상 입력해주세요.");
    }

    @Override
    public User findById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) throw new NoSuchElementException("해당 User가 존재하지 않습니다.");
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        System.out.println("현재 등록 User : " + users.size() + "명");
        return users;
    }

    @Override
    public User update(User user) {
        User updateUser = userRepository.findById(user.getId());
        if (updateUser == null) throw new NoSuchElementException("해당 User가 존재하지 않습니다.");
        updateUser.update(user);
        userRepository.save(updateUser);
        return updateUser;
    }

    @Override
    public void delete(UUID id) {
        if (userRepository.findById(id) == null) throw new NoSuchElementException("해당 User가 존재하지 않습니다.");
        userRepository.delete(id);
    }
}











