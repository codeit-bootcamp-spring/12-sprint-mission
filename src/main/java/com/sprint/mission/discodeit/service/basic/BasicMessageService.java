package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository repository;

    public BasicMessageService(MessageRepository repository){
        this.repository = repository;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId, content);
        repository.save(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(UUID id, String content) {
        Message message = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메시지 없음"));
        message.update(content);
        repository.save(message);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
