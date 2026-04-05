package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;

public class MessageServiceTest {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        User user = new User("yesung", "test1111@gmail.com", "1234");
        Channel channel = new Channel("FirstTest", new ArrayList<>());

        userService.create(user);
        channelService.create(channel);

        // 1. 정상 전송
        Message message1 = new Message("안녕하세요", user, channel);
        messageService.send(message1);
        System.out.println("정상 전송 성공: " + message1.getDisplayMessage());

        // 2. 존재하지 않는 사용자
        try {
            User fakeUser = new User("fakeUser", "test2222@gmail.com", "1234");
            Message message2 = new Message("실패", fakeUser, channel);
            messageService.send(message2);
        } catch (IllegalArgumentException e) {
            System.out.println("사용자 검증 성공: " + e.getMessage());
        }

        // 3. 존재하지 않는 채널
        try {
            Channel fakeChannel = new Channel("fakeChannel", new ArrayList<>());
            Message message3 = new Message("실패", user, fakeChannel);
            messageService.send(message3);
        } catch (IllegalArgumentException e) {
            System.out.println("채널 검증 성공: " + e.getMessage());
        }

        // 4. 빈 메시지
        try {
            Message message4 = new Message("", user, channel);
            messageService.send(message4);
        } catch (IllegalArgumentException e) {
            System.out.println("내용 검증 성공: " + e.getMessage());
        }
    }
}