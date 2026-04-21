package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public void save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
    }

    @Override
    public UserStatus findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}