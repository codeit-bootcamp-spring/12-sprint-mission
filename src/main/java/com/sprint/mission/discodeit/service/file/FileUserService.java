package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final String filename = "users.data";
    private Map<UUID, User> data;

    public FileUserService() {
        this.data = readFromFile();
    }

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        data.put(user.getId(), user);
        writeToFile();
        return user;
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
    public void update(UUID id, String username, String email, String password) {
        User user = data.get(id);
        if(user == null){
            throw new RuntimeException("유저 없음");
        }
        user.update(username, email, password);
        writeToFile();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        writeToFile();
    }

    private void writeToFile(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(data);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private Map<UUID, User> readFromFile(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))){
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

}
