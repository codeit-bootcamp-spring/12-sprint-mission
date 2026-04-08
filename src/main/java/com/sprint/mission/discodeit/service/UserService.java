package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User save(User user); // 등록
    User findById(UUID id); // 조회(단건)
    List<User> findAll(); // 조회(다건)
    User update(User user, UUID loginUserId); // 수정
    void deleteByID(UUID id, UUID loginUserId); // 삭제(단건)
}