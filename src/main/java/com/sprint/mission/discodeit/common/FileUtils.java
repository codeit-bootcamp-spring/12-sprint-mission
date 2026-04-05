package com.sprint.mission.discodeit.common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패");
            }
        }
    }

    public static void saveObject(Path path, Object obj) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException("객체 저장 실패");
        }
    }

    public static Object loadObject(Path path) {
        if (!Files.exists(path)) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("객체 불러오기 실패");
        }
    }
}
