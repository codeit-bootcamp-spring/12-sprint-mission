package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserRepository {
    // 1. 유저 저장하기
    void save(User user);

    // 2. ID로 유저 찾기
    User findById(UUID id);

    // 3. 모든 유저 목록 가져오기
    List<User> findAll();

    // 4. 유저 정보 수정
    void update(User user);

    // 5. 유저 삭제
    void delete(UUID id);
}