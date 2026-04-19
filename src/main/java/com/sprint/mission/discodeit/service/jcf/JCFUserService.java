package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        for(User user : data) {
            if(user.getId().equals(id)) return Optional.of(user);
        }
//        data.stream().filter(idata -> idata.getId().equals(id));
        return null;
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
        Optional<User> found = findById(id);
        if (found.isPresent()) {
            found.get().update(user.getUserName(), user.getPassword(), user.getEmail(), user.getNickName(), user.getProfileId());
            return found.orElse(null);
        }
        return null;
    }
}
