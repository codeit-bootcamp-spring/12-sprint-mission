package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final File dir = new File("file");
    private final File channelFile = new File("file", "channel.dat");

    public FileChannelRepository() {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            if (!channelFile.exists()) {
                channelFile.createNewFile();
            }
        } catch (Exception e) {
            throw new RuntimeException("파일을 생성할 수 없습니다.");
        }
    }

    private Map<UUID, Channel> readChannel() {
        if (channelFile.length() == 0) {
            return new HashMap<>();
        }
        try (FileInputStream fis = new FileInputStream(channelFile);
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            Map<UUID, Channel> channelMap = (Map<UUID, Channel>) ois.readObject();
            return channelMap;
        } catch (Exception e) {
            throw new RuntimeException("파일을 읽을 수 없습니다");
        }
    }

    private void writeChannel(Map<UUID, Channel> channelMap) {
        try (FileOutputStream fos = new FileOutputStream(channelFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(channelMap);
        } catch (Exception e) {
            throw new RuntimeException("파일을 저장할 수 없습니다.");
        }
    }

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> channelMap = readChannel();
        channelMap.put(channel.getChannelId(), channel);
        writeChannel(channelMap);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Map<UUID, Channel> channelMap = readChannel();
        return channelMap.get(id);
    }

    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> channelMap = readChannel();
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> channelMap = readChannel();
        channelMap.remove(id);
        writeChannel(channelMap);
    }
}
