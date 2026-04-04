package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // ------------------ 등록 ----------------------
        // 사용자 (User) 테스트 시작!!
        System.out.println("-------------사용자 테스트 시작!!!---------------");
//        UserService userService = new JCFUserService(); // JCF 기반
        UserService userService = new FileUserService(); // File 기반
        User user = new User("test.com", "1234", "kim", "kk");
        userService.save(user);

        User user2 = new User("test2.com", "1234", "kim", "kk");
        userService.save(user2);

        User user3 = new User("test2.com", "1234", "kim", "kk");
        userService.save(user3);

        System.out.println(userService.findAll());
        System.out.println("-------------사용자 테스트 끝!!!----------------\n");

        // 채널 (Channel) 테스트 시작!!
        System.out.println("-------------채널 테스트 시작!!!---------------");
//        ChannelService channelService = new JCFChannelService(); // JCF 기반
        ChannelService channelService = new FileChannelService(); // File 기반

        // TEXT, VOICE, FORUM 채널 타입
        Channel channel = new Channel("텍스트 채널", Channel.ChannelType.TEXT, true);
        channelService.save(channel);

        Channel channel2 = new Channel("음성 채널", Channel.ChannelType.VOICE, true);
        channelService.save(channel2);

        Channel channel3 = new Channel("포럼 채널", Channel.ChannelType.FORUM, false);
        channelService.save(channel3);

        System.out.println(channelService.findAll());
        System.out.println("-------------채널 테스트 끝!!!----------------\n");

        // 메세지 (Message) 테스트 시작!!
        System.out.println("-------------메세지 테스트 시작!!!---------------");
//        MessageService messageService = new JCFMessageService(userService, channelService); // message의 user와 channel 의존성 // JCF 기반
        MessageService messageService = new FileMessageService(userService, channelService); //  File 기반

        // TEXT, VOICE, FORUM 채널 타입
        Message message = new Message(channel.getId(), UUID.randomUUID(), user.getId(), "메세지 테스트 1");
        messageService.save(message);

        Message message2 = new Message(channel2.getId(), UUID.randomUUID(), user2.getId(), "메세지 테스트 2");
        messageService.save(message2);

        Message message3 = new Message(channel3.getId(), UUID.randomUUID(), user3.getId(), "메세지 테스트 3");
        messageService.save(message3);

        System.out.println(channelService.findAll());
        System.out.println("-------------메세지 테스트 끝!!!----------------\n");


        // ------------------ 조회 ----------------------
        UUID testId = UUID.fromString("00000000-0000-0000-0000-000000000000"); // 임의의 UUID
        System.out.println("-------------조회 시작!!!----------------");
        System.out.println(userService.findById(testId));
        System.out.println(userService.findById(user2.getId()));
        System.out.println(messageService.findAll());
        System.out.println(channelService.findAll());
        System.out.println("-------------조회 끝!!!----------------");

        // ------------------ 수정 ----------------------
        System.out.println("-------------수정 시작!!!----------------");
        System.out.println(userService.findById(user3.getId()));
        User updateUser = new User("김수정", "1357", "edit@mail.com", "editNickName");
        userService.update(user3.getId(), updateUser);

        // ------------------ 수정된 데이터 조회 ----------------------
        System.out.println(userService.findById(user3.getId()));
        System.out.println("-------------수정 끝!!!----------------");

        // ------------------ 삭제 ----------------------
        System.out.println("-------------삭제 시작!!!----------------");
        System.out.println(channelService.findAll());
        channelService.delete(channel.getId());

        // ------------------ 삭제된 데이터 조회----------------------
        System.out.println(channelService.findAll());
        System.out.println("-------------삭제 끝!!!----------------");

        // 메세지 의존성 테스트
        System.out.println("-------------메세지 의존성 테스트 시작!!!----------------");
        Message message4 = new Message(channel.getId()/* 전에 삭제한 채널 */, UUID.randomUUID(), user3.getId(), "메세지 의존성 테스트1");
        if(messageService.save(message4) == null) System.out.println("저장 실패!");
        Message message5 = new Message(channel2.getId(), UUID.randomUUID(), UUID.randomUUID() /*원래 유저 id 들어감*/, "메세지 의존성 테스트2");
        if(messageService.save(message5) == null) System.out.println("저장 실패!");
        System.out.println("-------------메세지 의존성 테스트 끝!!!----------------");
    }
}
