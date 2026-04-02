package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService,channelService);

        // 사용자 테스트 시작!!
        System.out.println("----------------사용자 테스트 시작!!!------------------");

        System.out.println("1. USER 생성");

        User user1 = userService.create("test1", "test1@email.com", "1234", "test");
        User user2 = userService.create("test2", "test2@email.com", "1234", "test");
        User user3 = userService.create("test3", "test3@email.com", "1234", "test");

        System.out.println(userService.findAll());

        System.out.println("2. CHANNEL 생성");
        Channel channel1 = channelService.create("일반채널");
        Channel channel2 = channelService.create("공지채널");

        System.out.println(channelService.findAll());

        System.out.println("3. MESSAGE 생성");
        Message message1 = messageService.create(user1.getId(), channel1.getId(), "안녕하세요");
        Message message2 = messageService.create(user2.getId(), channel2.getId(), "반갑습니다");
        Message message3 = messageService.create(user3.getId(), channel2.getId(), "안녕히계세요");

        System.out.println(messageService.findAll());

        System.out.println("4. 단건 조회");
        userService.findById(user1.getId())
                        .ifPresent(user -> System.out.println("유저 조회 : " + user));
        channelService.findById(channel1.getId())
                        .ifPresent(channel -> System.out.println("채널 조회 : " + channel));
        messageService.findById(message1.getId())
                        .ifPresent(message -> System.out.println("메시지 조회 : " + message));

        System.out.println("5. 수정");
        userService.update(user1.getId(), "test4", "test4@email.com", "12345","testtest");
        channelService.update(channel1.getId(), "비밀채널");
        messageService.update(message1.getId(), "처음뵙겠습니다");

        System.out.println("6. 수정 후 조회");
        userService.findById(user1.getId())
                .ifPresent(user -> System.out.println("유저 수정 조회 : " + user));
        channelService.findById(channel1.getId())
                .ifPresent(channel -> System.out.println("채널 수정 조회 : " + channel));
        messageService.findById(message1.getId())
                .ifPresent(message -> System.out.println("메시지 수정 조회 : " + message));

        System.out.println("7. 삭제");
        userService.delete(user1.getId());
        channelService.delete(channel1.getId());
        messageService.delete(message1.getId());

        System.out.println("8. 삭제 후 전체 조회");
        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());

        System.out.println("---------------사용자 테스트 끝!!!---------------------\n");
    }
}