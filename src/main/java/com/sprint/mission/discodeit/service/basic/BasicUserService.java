package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository repository;

    public BasicUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        repository.save(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(UUID id, String name, String email, String password) {
        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        user.update(name, email, password);
        repository.save(user);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}