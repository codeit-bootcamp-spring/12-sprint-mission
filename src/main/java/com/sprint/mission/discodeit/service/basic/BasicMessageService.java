package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public BasicMessageService() {
        messageRepository = new FileMessageRepository();
    }

    @Override
    public UUID create(Message message) {
        messageRepository.save(message);
        return message.getId();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public Optional<List<Message>> findBySendUser(User user) {
        return findAll().map(
                messages ->
                        messages.stream().filter(message -> message.getSendUser().equals(user)).toList()
        );
    }

    @Override
    public Optional<List<Message>> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public void updateById(UUID id, String content) {
        messageRepository.findById(id).ifPresentOrElse(
                message -> {
                    message.update(content);
                    messageRepository.save(message);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Message가 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        messageRepository.deleteById(id);
    }
}
