package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User save (User user);
    User findById(UUID id);
    List<User> findAll();
    User update(UUID id, String name, String email, String password, String nickname);
    void delete(UUID id);
    User create(String name, String email, String password, String nickname); // test 편하게 하려고 만들었는데 나중에 삭제하고 save로 통합 시킬계획입니다.
}
