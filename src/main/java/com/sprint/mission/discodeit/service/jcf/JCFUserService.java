package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // 일단 Username만 임시제작
    public User update(UUID id, String newUserName) {
        User user = findById(id);
        if (user != null) {
            user.update(newUserName,null,null,null);
        }
        return user;
    }

    // 삭제(delete) 예시
    public void delete(UUID id) {
        data.remove(id);
    }
}
