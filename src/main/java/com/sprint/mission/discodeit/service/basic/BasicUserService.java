package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService() {
        userRepository = new FileUserRepository();
    }

    @Override
    public UUID create(User user) {
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<List<User>> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void updateById(UUID id, String username, String email, String password, String nickname) {
        userRepository.findById(id).ifPresentOrElse(
                user -> {
                    user.update(username, email, password, nickname);
                    userRepository.save(user);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 User가 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
