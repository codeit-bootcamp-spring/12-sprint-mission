package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data;

    public JCFMessageRepository(){
        data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        for(Message message : data) {
            if(message.getId().equals(id)) return Optional.of(message);
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }

    @Override
    public Message update(UUID id, Message message) {
        Optional<Message> OptionalMessage = findById(id);
        if (OptionalMessage.isPresent()) {
            Message found = OptionalMessage.get();
            found.update(message.getContent());
            return found;
        }
        return null;
    }

    @Override
    public boolean delete(UUID id) {
        for(Message message : data) {
            if(message.getId().equals(id)) {
                data.remove(message);
                return true;
            }
        }
        return false;
    }
}
