package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;

    public JCFUserRepository() { data = new ArrayList<>(); }

    @Override
    public void save(User user) {
        findById(user.getId()).ifPresent(data::remove);
        data.add(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<List<User>> findAll() {
        return Optional.of(data);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresent(data::remove);
    }
}
