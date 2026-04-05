package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageService implements MessageService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "messages");
        FileUtils.init(DIRECTORY);

        this.userService = userService;
        this.channelService = channelService;
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        if (userService.findById(message.getUserId()) == null) {
            throw new NoSuchElementException("존재하지 않는 사용자");
        }
        if (channelService.findById(message.getChannelId()) == null) {
            throw new NoSuchElementException("존재하지 않는 채널");
        }

        Path path = makePath(message.getId());
        try {
            FileUtils.saveObject(path, message);
        } catch (RuntimeException e) {
            throw new RuntimeException("메시지 저장 실패");
        }
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return (Message) FileUtils.loadObject(makePath(messageId));
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Message) FileUtils.loadObject(path))
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없음");
        }
    }

    @Override
    public void update(UUID messageId, String content) {
        Message message = findById(messageId);
        message.update(content);

        Path path = makePath(messageId);
        try {
            FileUtils.saveObject(path, message);
        } catch (RuntimeException e) {
            throw new RuntimeException("메시지 정보 변경 실패");
        }
    }

    @Override
    public void delete(UUID messageId) {
        Path path = makePath(messageId);

        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패");
        }
    }
}
