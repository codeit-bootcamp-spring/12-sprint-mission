package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "jcf")
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User save(User user) {
        if (user == null) throw new NoSuchElementException("User 객체가 비어있습니다.");
        if (user.getId() == null) throw new IllegalArgumentException("User ID를 찾을 수 없습니다.");
        data.put(user.getId(), user);
        return data.get(user.getId());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsByNickname(String nickname) {
        boolean result = false;
        for(User user : data.values()){
            if (user.getNickname().equals(nickname)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean existsById(UUID id) {
        return data.get(id) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        boolean result = false;
        for(User user : data.values()){
            if (user.getEmail().equals(email)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public Optional<User> findByNameAndPassword(String name, String password) {
        return data.values().stream()
                .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
