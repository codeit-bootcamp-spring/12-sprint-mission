package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService(List<Message> data) {
        this.data = data;
    }

    @Override
    public Message save(Message message) {
        return null;
    }

    @Override
    public Message findById(UUID id) {
        return null;
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public Message update(User user) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
