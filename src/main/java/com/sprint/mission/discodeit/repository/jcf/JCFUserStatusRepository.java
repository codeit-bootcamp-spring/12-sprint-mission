package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> userStatusMap = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(userStatusMap.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus userStatus : userStatusMap.values()) {
            if (userStatus.getUserId().equals(userId)) {
                return Optional.of(userStatus);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        List<UserStatus> userStatusList = new ArrayList<>();
        for (UserStatus userStatus : userStatusMap.values()) {
            userStatusList.add(userStatus);
        }
        return userStatusList;
    }


    @Override
    public void delete(UUID id) {
        userStatusMap.remove(id);

    }
}
