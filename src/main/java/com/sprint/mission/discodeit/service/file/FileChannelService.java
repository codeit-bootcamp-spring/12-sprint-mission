package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final String filename = "channels.data";
    private Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = readFromFile();
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        data.put(channel.getId(), channel);
        writeToFile();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, ChannelType type, String name, String description) {
        Channel channel = data.get(id);
        if(channel == null){
            throw new RuntimeException("채널 없음");
        }
        channel.update(type, name, description);
        writeToFile();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        writeToFile();
    }

    public void writeToFile(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID,Channel> readFromFile(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))){
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
