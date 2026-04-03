package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository,
                               UserRepository userRepository,
                               ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message save(Message message) {
        // 1. 객체 null 체크
        if (message == null) {
            System.err.println("입력된 메시지 객체가 null입니다.");
            return null;
        }

        // 2. 작성 유저 존재 여부 확인
        if (userRepository.findById(message.getUserId()).isEmpty()) {
            System.err.println("존재하지 않는 유저의 메시지입니다.");
            return null;
        }

        // 3. 대상 채널 존재 여부 확인
        if (channelRepository.findById(message.getChannelId()).isEmpty()) {
            System.err.println("존재하지 않는 채널의 메시지입니다.");
            return null;
        }

        // 4. 내용 공백 체크
        if (message.getContent() == null || message.getContent().isBlank()) {
            System.err.println("메시지 내용은 비어 있을 수 없습니다.");
            return null;
        }

        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElse(null);
                //.orElseThrow(() -> new NoSuchElementException("해당 ID의 메시지를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId) {
        // 수정 전 해당 메시지가 존재하는지 확인
        if (messageRepository.findById(messageId).isEmpty()) {
            System.err.println("수정하려는 메시지가 존재하지 않습니다.");
            return null;
        }

        if (channelId != null && channelRepository.findById(channelId).isEmpty()) {
            System.err.println("변경하려는 채널이 존재하지 않습니다.");
            return null;
        }

        if (content == null) {
            System.err.println("null 값으로는 변경이 불가합니다.");
            return null;
        }

        return messageRepository.update(messageId, content, channelId);
    }

    @Override
    public void delete(UUID messageId) {
        findById(messageId);
        messageRepository.delete(messageId);
    }
}