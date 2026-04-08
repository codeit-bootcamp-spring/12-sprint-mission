package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String password);
    User findById(UUID id);
    List<User> findAll();
    User updateUsername(UUID id , String name);
    User updateEmail(UUID id, String email);
    User updatePassword(UUID id, String password);
    void deleteById(UUID id);





}