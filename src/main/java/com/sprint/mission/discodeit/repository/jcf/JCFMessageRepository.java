package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "jcf")
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        if (message == null) throw new NoSuchElementException("Message 객체가 비어있습니다.");
        if (message.getId() == null) throw new IllegalArgumentException("Message ID를 찾을 수 없습니다.");
        data.put(message.getId(), message);
        return data.get(message.getId());
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findByChannelId(UUID id) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(id))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return data.get(id) !=null;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
