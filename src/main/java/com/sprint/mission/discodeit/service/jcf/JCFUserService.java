package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public UUID create(User user) {
        data.add(user);
        return user.getId();
    }

    @Override
    public Optional<User> findById(UUID id) {
        for (User user : data) {
            if(user.getId().equals(id)) return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<User>> findAll() {
        if (!data.isEmpty()) {
            return Optional.of(data);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void updateById(UUID id, String username, String email, String password, String nickname) {
        findById(id).ifPresentOrElse(
                (user) -> user.update(username, email, password, nickname),
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 User가 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresentOrElse(
                data::remove,
                () -> System.out.println("\t삭제실패 : 입력된 id(" + id + ")에 해당하는 User가 없습니다")
        );
    }
}
