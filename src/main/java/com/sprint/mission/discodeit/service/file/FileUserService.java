package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileUserService {
    public static void main(String[] args) throws FileNotFoundException {
        User u = new User("JaneDoe", "JaneDoe@gmail.com",9876,"JD1");
        System.out.println(u);
        try(FileOutputStream fos = new FileOutputStream("user.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(u);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
