package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    // 메시지 저장 공간
    private final Map<UUID, Message> data;

    // 다른 서비스 의존성 ( 유저, 채널 확인용 )
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자에서 의존성 주입
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }
    // 메시지 생성
    @Override
    public Message create(Message message) {
        // 유저 존재 여부 확인
        User user = userService.findById(message.getUser().getId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자");
        }

        //  채널 존재 여부 확인
        Channel channel = channelService.findById(message.getChannel().getId());
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널");
        }

//        //  채널에 속한 유저인지 확인 !
//        if (!channel.getUsers().contains(user)) {
//            throw new IllegalArgumentException("채널에 속하지 않은 사용자");
//        }

        // 저장
        data.put(message.getId(), message);
        return message;

    }

    // 단건 조회
    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    // 전체 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    // 삭제
    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public Message update(UUID id, Message message) {
        data.put(id, message);
        return message;
    }
}
