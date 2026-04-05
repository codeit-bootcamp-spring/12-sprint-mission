package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userRepo;

    public JCFUserService(){
        this.userRepo = new HashMap<>();
    }

    @Override
    public User create(User user) {
        userRepo.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return userRepo.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userRepo.values());
    }

    // 부분 수정?
    @Override
    public User update(UUID id, String username, String email, String password) {
        User existingUser = userRepo.get(id);

        if (existingUser == null) {
            return null;
        }


        if (username != null) {
            existingUser.updateUsername(username);
        }

        if (email != null) {
            existingUser.updateEmail(email);
        }


        if (password != null) {
            existingUser.updatePassword(password);
        }

        return existingUser;
    }

    @Override
    public void delete(UUID id) {
        userRepo.remove(id);
    }
}
