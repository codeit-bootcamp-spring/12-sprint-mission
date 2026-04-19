package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;

    public JCFUserRepository() {
        data = new ArrayList<>();
    }

    @Override
    public User create(User user) {
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        for(User user : data) {
            if(user.getId().equals(id)) return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data);
    }

    @Override
    public boolean delete(UUID id) {
        for(User user : data) {
            if(user.getId().equals(id)) {
                data.remove(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public User update(UUID id, User user) {
        Optional<User> OptionalUser = findById(id);
        if (OptionalUser.isPresent()) {
            User found = OptionalUser.get();
            found.update(user.getUserName(), user.getPassword(), user.getEmail(), user.getNickName(), user.getProfileId());
            return found;
        }
        return null;
    }
}
