package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message save(Message message) {
        if (messageRepository.findById(message.getId()) != null)
            throw new IllegalStateException("이미 동일한 id의 Message가 존재합니다.");
        validateMessage(message);
        userService.findById(message.getAuthor().getId());
        channelService.findById(message.getCh().getId());
        return messageRepository.save(message);
    }

    private void validateMessage(Message message) {
        if (message.getTitle() == null || message.getTitle().isBlank())
            throw new IllegalArgumentException("제목은 필수 입력 항목 입니다.");
        if (message.getContent() == null || message.getContent().isBlank())
            throw new IllegalArgumentException("내용은 필수 입력 항목 입니다.");
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) throw new NoSuchElementException("해당 Message가 존재하지 않습니다.");
        return message;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = messageRepository.findAll();
        System.out.println("현재 등록 Message : " + messages.size() + "개");
        return messages;
    }

    @Override
    public Message update(Message message) {
        Message updateMessage = messageRepository.findById(message.getId());
        if (updateMessage == null) throw new NoSuchElementException("해당 Message가 존재하지 않습니다.");
        updateMessage.update(message);
        messageRepository.save(updateMessage);
        return updateMessage;
    }

    @Override
    public void delete(UUID id) {
        if (messageRepository.findById(id) == null) throw new NoSuchElementException("해당 Message가 존재하지 않습니다.");
        messageRepository.delete(id);
    }
}











