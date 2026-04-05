package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;

        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "my_dir", "messages");
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message sendMessage(UUID channelId, UUID authorId, String content) {
        // [비즈니스 로직]
        if (channelService.getChannel(channelId) == null) {
            System.out.println("존재하지 않는 채팅방입니다.");
            return null;
        }
        if (userService.getUser(authorId) == null) {
            System.out.println("존재하지 않는 유저입니다.");
            return null;
        }

        Message message = new Message(channelId, authorId, content);

        // [저장 로직]
        Path path = makePath(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            System.out.println("메시지 저장에 실패하였습니다.");
        }
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        Path path = makePath(id);
        if (Files.notExists(path)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Message) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    private List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIRECTORY,
                path -> path.toString().endsWith(EXTENSION))) {
            for (Path path : stream) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    messages.add((Message) ois.readObject());
                } catch (Exception e) {
                    System.out.println("일부 메시지를 불러오지 못했습니다.");
                }
            }
        } catch (IOException e) {
            System.out.println("메시지 목록 조회 실패");
        }
        return messages;
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : getAllMessages()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = getMessage(id);
        if (message != null) {
            message.update(content);

            Path path = makePath(id);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                oos.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        try {
            Files.deleteIfExists(makePath(id));
        } catch (IOException e) {
            System.out.println("메시지 삭제에 실패하였습니다.");
        }
    }
}