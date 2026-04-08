package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final File dir = new File("file");
    private final File userFile = new File(dir, "user.dat");

    public FileUserRepository() {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            if (!userFile.exists()) {
                userFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("파일을 생성할 수 없습니다.");
        }
    }

    private Map<UUID, User> readUser() {
        if (userFile.length() == 0) {
            return new HashMap<>();
        }
        try (FileInputStream fis = new FileInputStream(userFile);
             ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            Map<UUID, User> userMap = (Map<UUID, User>) ois.readObject();
            return userMap;
        } catch (Exception e) {
            throw new RuntimeException("파일을 읽을 수 없습니다");
        }
    }

    private void writeUser(Map<UUID, User> userMap) {
        try (FileOutputStream fos = new FileOutputStream(userFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(userMap);
        } catch (Exception e) {
            throw new RuntimeException("파일을 저장할 수 없습니다");
        }
    }

    @Override
    public User save(User user) {
        Map<UUID, User> userMap = readUser();
        userMap.put(user.getUserId(), user);
        writeUser(userMap);
        return user;
    }

    @Override
    public User findById(UUID id) {
        Map<UUID, User> userMap = readUser();
        return userMap.get(id);

    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> userMap = readUser();
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> userMap = readUser();
        userMap.remove(id);
        writeUser(userMap);
    }
}
