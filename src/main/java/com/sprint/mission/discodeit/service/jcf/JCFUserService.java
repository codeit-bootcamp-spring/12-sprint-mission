package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    // 등록
    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    // 조회(단건)
    @Override
    public User findById(UUID id) {
      return data.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    // 조회(다건)
    @Override
    public List<User> findAll() {
        return data;
    }

    // 수정
    @Override
    public User update(User user, UUID loginUserId) {
        User userToUpdate = findById(user.getId());

        if(userToUpdate == null) {
            throw new NoSuchElementException("수정할 유저를 찾을 수 없습니다.");
        }

        if(!userToUpdate.getId().equals(loginUserId)) {
            throw new IllegalArgumentException("본인만 수정할 수 있습니다!");
        }

        userToUpdate.update(
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getNickname()
        );

        return userToUpdate;
    }

    // 삭제(단건)
    @Override
    public void deleteByID(UUID id, UUID loginUserId) {
        User userToDelete = findById(id);

        if(userToDelete == null) {
            throw new NoSuchElementException("삭제할 유저가 없습니다!");
        }

        if(!userToDelete.getId().equals(loginUserId)) {
            throw new IllegalArgumentException("본인만 삭제할 수 있습니다.");
        }

        data.removeIf(user -> user.getId().equals(id));
    }
}