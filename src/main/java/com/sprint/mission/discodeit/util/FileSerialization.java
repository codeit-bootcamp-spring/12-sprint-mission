package com.sprint.mission.discodeit.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileSerialization {
    private static final Path path = Paths.get(System.getProperty("user.dir"), "data");

    public static <T> List<T> loadData(String filePath) {
        ensureDirectoryExists();

        Path file = path.resolve(filePath);

        if (Files.exists(file)) {
            try (
                    FileInputStream fis = new FileInputStream(file.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                return (List<T>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    public static <T> void saveData(String filePath, List<T> data) {
        ensureDirectoryExists();

        Path file = path.resolve(filePath);

        try (
                FileOutputStream fos = new FileOutputStream(file.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void ensureDirectoryExists() {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}