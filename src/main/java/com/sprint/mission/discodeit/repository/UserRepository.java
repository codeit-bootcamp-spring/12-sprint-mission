package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void save(User user);                               // 생성
    User findUserByNickname(String nickname);           // 조회
    User findUserByEmail(String email);                 // 조회
    List<User> findAllUsers();                          // 조회
    User changeUserNickname(UUID id, String nickname);  // 수정
    User deleteUser(UUID id);                           // 삭제
}
