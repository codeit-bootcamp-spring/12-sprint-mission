package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data;

    public JCFMessageRepository() { data = new ArrayList<>(); }

    @Override
    public void save(Message message) {
        findById(message.getId()).ifPresent(data::remove);
        data.add(message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return data.stream().filter(message -> message.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<List<Message>> findAll() {
        return Optional.of(data);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresent(data::remove);
    }
}
