package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;


    public class JCFUserRepository implements UserRepository {
        private static final String FILE_PATH = "user.ser";

        private final Map<UUID, User> data;

        public JCFUserRepository() {
            this.data = new HashMap<>();
        }

        @Override
        public void save(User user) {
            data.put(user.getId(), user);
        }

        @Override
        public User findUserByNickname(String nickname) {
            for (User user : data.values()) {
                if (user.getNickname().equals(nickname)) {
                    return user;
                }
            }

            throw new IllegalArgumentException("유저 없음.");
        }

        @Override
        public User findUserByEmail(String email) {
            for (User user : data.values()) {
                if (user.getEmail().equals(email)) {
                    return user;
                }
            }

            return null;
        }

        @Override
        public List<User> findAllUsers() {
            return new ArrayList<>(data.values());
        }

        @Override
        public User changeUserNickname(UUID id, String nickname) {
            User user = data.get(id);

            if (user == null) {
                throw new IllegalArgumentException("유저 없음.");
            }

            user.update(user.getUsername(), user.getPassword(), user.getEmail(), nickname);

            return user;
        }

        @Override
        public User deleteUser(UUID id) {
            User user = data.remove(id);

            if (user == null) {
                throw new IllegalArgumentException("유저 없음.");
            }

            return user;
        }
    }
