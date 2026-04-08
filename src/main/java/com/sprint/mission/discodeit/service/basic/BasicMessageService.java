package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final ChannelRepository channelRepo;

    public BasicMessageService(MessageRepository messageRepo, UserRepository userRepo, ChannelRepository channelRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.channelRepo = channelRepo;
    }

    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 공백일 수 없습니다.");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("작성자 아이디는 공백일 수 없습니다.");
        } else if (userRepo.findById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (channelId == null) {
            throw new IllegalArgumentException("채널 아이디는 공백일 수 없습니다.");
        } else if (channelRepo.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        User user = userRepo.findById(authorId);
        Channel channel = channelRepo.findById(channelId);
        Message message = new Message(content, user, channel);
        return messageRepo.save(message);
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageRepo.findById(id);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메세지입니다.");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messageList = messageRepo.findAll();
        messageList.sort((m1, m2) -> Long.compare(m1.getCreatedAt(), m2.getCreatedAt()));
        return messageList;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 공백일 수 없습니다.");
        }
        Message message = findById(id);
        message.updateContent(content);
        return messageRepo.save(message);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        messageRepo.deleteById(id);
    }
}
