package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    // 유저 생성
    User createUser(String username, String email, String password, String nickname);

    // 유저 조회
    User getUser(UUID id);
    List<User> getAllUsers();

    // 유저 정보 수정
    User updateUser(UUID id, String username, String email, String password, String nickname);

    // 유저 삭제
    void deleteUser(UUID id);
}