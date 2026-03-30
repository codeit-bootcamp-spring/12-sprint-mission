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
        for(User updateUser : data){
            if(updateUser.getId().equals(user.getId())){
                if(user.getName() != null) updateUser.updateName(user.getName());
                if(user.getNickname() != null) updateUser.updateNickname(user.getNickname());
                if(user.getEmail() != null) updateUser.updateEmail(user.getEmail());
                if(user.getPassword() != null) updateUser.updatePassword(user.getPassword());
                return updateUser;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
    }
}
