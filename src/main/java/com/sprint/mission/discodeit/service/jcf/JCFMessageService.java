package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data = new HashMap<>();
    private final ChannelService channelService;
    private final UserService userService;

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        channelService.find(channelId);
        userService.find(authorId);

        Message message = new Message(content, channelId, authorId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message find(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public Message update(UUID id, String newContent) {
        Message message = find(id);
        message.update(newContent);
        return message;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        data.remove(id);
    }
}