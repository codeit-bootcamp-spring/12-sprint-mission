package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final File file;

    public FileUserRepository() {
        this.file = new File("users.dat");
    }

    @Override
    public User save(User user) {
        List<User> users = readAll();
        boolean updated = false;
        for (int i = 0; i < users.size(); i++) {
           if (users.get(i).getId().equals(user.getId())) {
               users.set(i, user);
               updated = true;
               break;
           }
        }
        if (!updated) {
            users.add(user);
        }
        writeAll(users);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return readAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return readAll();
    }

    @Override
    public void delete(UUID id) {
        List<User> users = readAll();
        users.removeIf(user -> user.getId().equals(id));
        writeAll(users);
    }

    @SuppressWarnings("unchecked")
    private List<User> readAll() {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 파일 읽기에 실패했습니다.", e);
        }
    }

    private void writeAll(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("유저 파일 저장에 실패했습니다.", e);
        }
    }
}
