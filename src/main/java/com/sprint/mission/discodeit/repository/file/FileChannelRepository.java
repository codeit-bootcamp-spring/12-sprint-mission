package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final String BASEPATH = "./persistentfiles/channels";

    public FileChannelRepository() {
        File file = new File(BASEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void save(Channel channel) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BASEPATH + "/" + channel.getId().toString()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        File file = new File(BASEPATH + "/" + id.toString());
        if (!file.exists()) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return Optional.of((Channel) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<Channel>> findAll() {
        List<Channel> list = new ArrayList<>();
        File dir = new File(BASEPATH);
        File[] files = dir.listFiles();
        if (files == null) {
            return Optional.empty();
        }
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                list.add((Channel) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return Optional.of(list);
    }

    @Override
    public void deleteById(UUID id) {
        File file = new File(BASEPATH + "/" + id.toString());
        if (!file.exists()) {
            System.out.println(file.toPath() + " 파일이 없으므로 삭제할 수 없습니다");
            return;
        }
        file.delete();
    }
}
