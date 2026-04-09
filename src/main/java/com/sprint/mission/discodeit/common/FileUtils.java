package com.sprint.mission.discodeit.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static boolean saveObject(Path path, Object obj) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(obj);
            return true;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object loadObject(Path path) {
        if(!Files.exists(path)){
            throw new RuntimeException("Not found");
        }
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);){
            return ois.readObject();

        } catch (Exception e) {
            throw new RuntimeException("File io Error");
        }
    }

    public static void createDirectories(Path path){
        if(!Files.exists(path)){
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
