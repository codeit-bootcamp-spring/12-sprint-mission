package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final MessageRepository messageRepo = new JCFMessageRepository();
    private final UserRepository userRepo = new JCFUserRepository();
    private final ChannelRepository channelRepo = new JCFChannelRepository();


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
