package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService(){
        data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        for(User user : data){
            if(user.getId().equals(id)) return user;
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public User update(User user) {
        User updateUser = findById(user.getId());
        if(updateUser == null){
            return null;
        }
        updateUser.update(user);
        return updateUser;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
    }
}
