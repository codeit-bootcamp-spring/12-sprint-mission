package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.DTO.CreateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.EmailVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(CreateUserRequest request) {
        EmailVerifier.isValidEmail(request.email());

        for (User user : data.values()) {
            if (user.getEmail().equals(request.email())) {
                throw new IllegalArgumentException("이미 존재하는 이메일.");
            }
        }

        User user = new User(request.username(), request.password(), request.email(), request.nickname());
        data.put(user.getId(), user);

        return user;
    }

    @Override
    public User findUserByNickname(String nickname) {
        for (User user : data.values()) {
            if (user.getNickname().equals(nickname)) {
                return user;
            }
        }

        throw new IllegalArgumentException("유저 없음.");
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User changeUserNickname(UUID id, String nickname) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음.");
        }

        user.update(user.getUsername(), user.getPassword(), user.getEmail(), nickname);
        return user;
    }

    @Override
    public User deleteUser(UUID id) {
        User user = data.remove(id);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음.");
        }

        return user;
    }
}