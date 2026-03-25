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

import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 사용자 테스트
        System.out.println("--------------- 사용자 테스트 시작! ---------------");
        UserService us = new JCFUserService();
        ChannelService cs = new JCFChannelService(us);
        MessageService ms = new JCFMessageService(cs,us);

        User user1 = new User("testUser1","testUser1@email.com","testNick1","1234");
        User user2 = new User("testUser2","testUser2@email.com","testNick2","2345");
        User user3 = new User("testUser3","testUser3@email.com","testNick3","3456");
        User user4 = new User("testUser4","testUser4@email.com","testNick4","4567");
        User user5 = new User("testUser5","testUser5@email.com","testNick5","5678");
        User user6 = new User("testUser6","testUser6@email.com","testNick6","6789");
        us.save(user1);
        us.save(user2);
        us.save(user3);
        us.save(user4);
        us.save(user5);
        us.save(user6);

        Channel channel1 = new Channel("First Channel",user1,"Notice");
        Channel channel2 = new Channel("Second Channel",user2,"Chat");
        Channel channel3 = new Channel("Third Channel",user3,"Voice");
        Channel channel4 = new Channel("Fourth Channel",user4,"Study");
        Channel channel5 = new Channel("Fifth Channel",user5,"Chat");
        Channel channel6 = new Channel("Sixth Channel",user6,"Study");
        cs.save(channel1);
        cs.save(channel2);
        cs.save(channel3);
        cs.save(channel4);
        cs.save(channel5);
        cs.save(channel6);

        Message message1 = new Message(channel1,user1,"testMessage1","first Test message");
        Message message2 = new Message(channel2,user2,"testMessage2","Second Test message");
        Message message3 = new Message(channel3,user3,"testMessage3","Third Test message");
        Message message4 = new Message(channel4,user4,"testMessage4","Fourth Test message");
        Message message5 = new Message(channel5,user5,"testMessage5","Fifth Test message");
        Message message6 = new Message(channel6,user6,"testMessage6","Sixth Test message");
        ms.save(message1);
        ms.save(message2);
        ms.save(message3);
        ms.save(message4);
        ms.save(message5);
        ms.save(message6);

        // 조회
        System.out.println("전체 조회");
        System.out.println(us.findAll().toString());
        System.out.println(cs.findAll().toString());
        System.out.println(ms.findAll().toString());

        System.out.println("부분조회 : user");
        System.out.println("user1 = "+us.findById(user1.getId()).toString());
        System.out.println("user3 = "+us.findById(user3.getId()).toString());
        System.out.println("user6 = "+us.findById(user6.getId()).toString());

        System.out.println("부분조회 : channel");
        System.out.println("channel2 = "+cs.findById(channel2.getId()).toString());
        System.out.println("channel1 = "+cs.findById(channel1.getId()).toString());
        System.out.println("channel4 = "+cs.findById(channel4.getId()).toString());

        System.out.println("부분조회 : message");
        System.out.println("message2 = "+ms.findById(message2.getId()).toString());
        System.out.println("message4 = "+ms.findById(message4.getId()).toString());
        System.out.println("message5 = "+ms.findById(message5.getId()).toString());

        // 수정
        System.out.println(new User("testUser1","testUser1@email.com","testNick1","1234"));
        System.out.println(new User("testUser2","testUser2@email.com","testNick2","2345"));
        System.out.println(new User("testUser3","testUser3@email.com","testNick3","3456"));
        System.out.println(new User("testUser4","testUser4@email.com","testNick4","4567"));
        System.out.println(new User("testUser5","testUser5@email.com","testNick5","5678"));
        System.out.println(new User("testUser6","testUser6@email.com","testNick6","6789"));


        System.out.println("--------------- 사용자 테스트 종료! ---------------");



    }

}
