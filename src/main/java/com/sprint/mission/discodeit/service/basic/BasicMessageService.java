package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public Message save(Message message) {
        if (message.getUserId() == null || message.getChannelId() == null) {
            throw new IllegalArgumentException("작성자 ID와 채널 ID는 필수입니다.");
        }
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new NoSuchElementException("해당 id의 메시지를 찾을 수 없습니다.");
        }
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(Message message) {
        findById(message.getId());

        if (message.getUserId() == null || message.getChannelId() == null) {
            throw new IllegalArgumentException("메시지 수정 시 작성자 ID와 채널 ID는 필수입니다.");
        }
        return messageRepository.update(message);
    }

    @Override
    public Message delete(UUID id) {
        findById(id);
        return messageRepository.delete(id);
    }
}
