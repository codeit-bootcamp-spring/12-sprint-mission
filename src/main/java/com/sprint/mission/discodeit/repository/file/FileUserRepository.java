package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final String filePath = "user.ser";
    private final Map<UUID, User> userMap = new HashMap<>();


    public FileUserRepository() {
        userMap.putAll(loadFromFile());
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        saveToFile();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void delete(UUID id) {
        userMap.remove(id);
        saveToFile();

    }

    private Map<UUID, User> loadFromFile() {
        File file = new File("user.ser");
        if (!file.exists()) return new HashMap<>();
        {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (Map<UUID, User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveToFile() {
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                        oos.writeObject(userMap);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }
            }
