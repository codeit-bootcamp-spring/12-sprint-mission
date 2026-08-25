package com.sprint.mission.discodeit.repository.queryDsl;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserQueryRepository {

    List<User> findAllWithProfile();

}
