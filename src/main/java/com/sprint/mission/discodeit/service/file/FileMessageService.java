package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.DTO.MessageData;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.FileSerialization;

import java.util.*;

public class FileMessageService implements MessageService {
    private static final String FILE_PATH = "message.ser";

    private final Map<UUID, Message> data;

    public FileMessageService() {
        this.data = new HashMap<>();

        List<Message> messageList = FileSerialization.<Message>loadData(FILE_PATH);
        for (Message msg : messageList) {
            data.put(msg.getId(), msg);
        }
    }

    @Override
    public Message createMessage(MessageData messageData) {
        Message message = new Message(messageData.authorId(), messageData.channelId(), messageData.content());

        data.put(message.getId(), message);
        FileSerialization.<Message>saveData(FILE_PATH, data.values().stream().toList());
        return message;
    }

    @Override
    public Message findMessageByContent(String content) {
        for (Message message : data.values()) {
            if (message.getContent().equals(content)) {
                return message;
            }
        }

        throw new IllegalArgumentException("메시지 없음.");
    }

    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message changeMessageContent(UUID id, String content) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지 없음.");
        }

        message.update(message.getAuthorId(), message.getChannelId(), content);
        return message;
    }

    @Override
    public Message deleteMessage(UUID id) {
        Message message = data.remove(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지 없음.");
        }

        return message;
    }
}