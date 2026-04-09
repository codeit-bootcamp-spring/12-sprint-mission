package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() { this.data = new HashMap<>(); }


    @Override
    public User create(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);
        this.data.put(user.getId(), user);

        return user;
    }


    @Override
    public User find(UUID userId) {
        User userNullable = this.data.get(userId);
            return Optional.ofNullable(userNullable)
                    .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }
    @Override
    public User update(UUID userID, String newUsername, String newEmail, String newPassword, String newNickname) {
        User userNullable = this.data.get(userID);
        User user = Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userID + " not found"));
        user.update(newUsername, newEmail, newPassword, newNickname);
        return user;
    }
    @Override
    public void delete(UUID userId) {
        if (!this.data.containsKey(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        this.data.remove(userId);
        }

}








