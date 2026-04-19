package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        User user = userRepository.findById(request.getUserId());
        Channel channel = channelRepository.findById(request.getChannelId());

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        Message message = new Message(
                request.getContent(),
                user,
                channel,
                request.getBinaryContentIds()
        );

        messageRepository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(UUID id, MessageUpdateRequest request) {
        Message target = messageRepository.findById(id);

        if (target == null) {
            throw new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }

        target.update(request.getContent());
        messageRepository.save(target);
        return target;
    }

    @Override
    public void delete(UUID id) {
        Message target = messageRepository.findById(id);

        if (target == null) {
            throw new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }

        for (UUID binaryId : target.getBinaryContentIds()) {
            binaryContentRepository.delete(binaryId);
        }

        messageRepository.delete(id);
    }
}