package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Primary
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + authorId + " not found"));

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));

        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        if (!messageRepository.existsById(id)) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        messageRepository.deleteById(id);
    }
}