package com.sprint.mission.discodeit.common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

    public static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> boolean saveObject(Path filePath, T object) {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(object);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            throw new RuntimeException("파일 읽기 실패");
        }
    }

    public static <T> List<T> load(Path directory) {
        if (!Files.exists(directory)) {
            return new ArrayList<>();
        }
        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .map(path -> (T) loadObject(path))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 읽기 실패" + e);
        }

    }
}
