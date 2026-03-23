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
        // 사용자 테스트
        System.out.println("--------------- 사용자 테스트 시작! ---------------");
        UserService userService = new JCFUserService();
        MessageService messageService = new JCFMessageService();
        ChannelService channelService = new JCFChannelService();

        User user = new User("testUser1","testUser1@email.com","testNick","1234");
        Channel channel = new Channel(user,"Notice");
        Message message = new Message(channel,user,"testMessage","first Test message");
        userService.save(user);
        channelService.save(channel);
        messageService.save(message);
        System.out.println(userService.findAll().toString());
        System.out.println(channelService.findAll().toString());
        System.out.println(messageService.findAll().toString());
        System.out.println("--------------- 사용자 테스트 종료! ---------------");
    }
}
