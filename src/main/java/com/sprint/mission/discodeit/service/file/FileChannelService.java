package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final String BASEPATH = "./persistentfiles/channels";

    public FileChannelService() {
        File file = new File(BASEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public UUID create(Channel channel) {
        saveToFile(channel);
        return channel.getId();
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(loadFromFile(id.toString()));
    }

    @Override
    public Optional<List<Channel>> findAll() {
        return Optional.ofNullable(loadAllFromFile());
    }

    @Override
    public void updateById(UUID id, String channelName, String description, User owner) {
        findById(id).ifPresentOrElse(
                (channel) -> {
                    channel.update(channelName, description, owner);
                    saveToFile(channel);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void addUser(UUID id, User user) {
        findById(id).ifPresentOrElse(
                (channel) -> {
                    channel.addUser(user);
                    saveToFile(channel);
                },
                () -> System.out.println("\t사용자추가 실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteUser(UUID id, User user) {
        findById(id).ifPresentOrElse(
                (channel) -> {
                    channel.deleteUser(user);
                    saveToFile(channel);
                },
                () -> System.out.println("\t사용자삭제 실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void addMessage(UUID id, Message message) {
        findById(id).ifPresentOrElse(
                (channel) -> {
                    channel.addMessage(message);
                    saveToFile(channel);
                },
                () -> System.out.println("\t메시지추가 실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteMessage(UUID id, Message message) {
        findById(id).ifPresentOrElse(
                (channel) -> {
                    channel.deleteMessage(message);
                    saveToFile(channel);
                },
                () -> System.out.println("\t메시지삭제 실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresentOrElse(
                channel -> {
                    deleteFile(channel.getId().toString());
                },
                () -> System.out.println("\t삭제실패 : 입력된 id(" + id + ")에 해당하는 Message가 없습니다")
        );
    }



    private List<String> loadAllFiles() {
        File file = new File(BASEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        String[] list = file.list();
        if (list==null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(List.of(list));
        }
    }

    private void saveToFile(Channel channel) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BASEPATH + "/" + channel.getId().toString()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Channel loadFromFile(String id) {
        File file = new File(BASEPATH + "/" + id);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<Channel> loadAllFromFile() {
        List<Channel> list = new ArrayList<>();
        File dir = new File(BASEPATH);
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                list.add((Channel) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    private void deleteFile(String id) {
        File file = new File(BASEPATH + "/" + id);
        if (!file.exists()) {
            System.out.println(file.toPath() + " 파일이 없으므로 삭제할 수 없습니다");
            throw new RuntimeException();
        }
        file.delete();
    }
}
