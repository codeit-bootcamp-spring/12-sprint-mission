package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message create(Message message) {
        return message;
    }

    @Override
    public Message read(UUID id, Message message) {
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, Message updatedMessage) {
        data.put(id, updatedMessage);
        return updatedMessage;}

    @Override
    public void delete(String message) { data.remove(message); }

    }
