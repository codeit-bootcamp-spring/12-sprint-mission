package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private final File file;

    public FileChannelRepository() {
        this.file = new File("channels.dat");
    }

    @Override
    public Channel save(Channel channel) {
        List<Channel> channels = readAll();

        boolean updated = false;
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getId().equals(channel.getId())) {
                channels.set(i, channel);
                updated = true;
                break;
            }
        }
        if (!updated) {
            channels.add(channel);
        }
        writeAll(channels);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return readAll().stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return readAll();
    }

    @Override
    public void delete(UUID id) {
        List<Channel> channels = readAll();
        channels.removeIf(channel -> channel.getId().equals(id));
        writeAll(channels);
    }

    @SuppressWarnings("unckecked")
    private List<Channel> readAll() {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 파일 읽기에 실패했습니다.", e);
        }
    }

    private void writeAll(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 저장에 실패했습니다.", e);
        }
    }
}
