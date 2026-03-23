package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.CreateUserRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(CreateUserRequest request);         // 생성
    User findUserByNickname(String nickname);           // 조회
    List<User> findAllUsers();                          // 조회
    User changeUserNickname(UUID id, String nickname);  // 수정
    User deleteUser(UUID id);                           // 삭제
}