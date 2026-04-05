package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // 생성, 읽기, 모두 읽기, 수정 삭제
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    User update(UUID id, String username, String email);
    boolean deleteById(UUID id);
}
