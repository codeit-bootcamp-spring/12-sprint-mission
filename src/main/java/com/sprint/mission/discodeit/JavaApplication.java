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
        MessageService messageService = new JCFMessageService(channelService, userService);

        // User Service 구현 테스트
        // 등록
        User user1 = new User("user1", "user1@email.com", "12345", "nick1");
        userService.create(user1);

        User user2 = new User("user2", "user2@email.com", "12345", "nick2");
        userService.create(user2);

        User user3 = new User("user3", "user3@email.com", "12345", "nick3");
        userService.create(user3);


        // 조회(단건)
        System.out.println("----------------------- user1 조회 -----------------------");
        System.out.println(userService.findById(user1.getId()));


        // 조회(다건)
        System.out.println("----------------------- 모든 User 조회 -----------------------");
        System.out.println(userService.findAll());


        // 수정
        user1.update("user12", "user12@email.com", "23451", "nick12");

        // 수정된 데이터 조회
        System.out.println("-------------------- 수정된 user1 조회 --------------------");
        System.out.println(userService.findById(user1.getId()));

        // 삭제
        userService.delete(user1.getId());

        // 조회를 통해 삭제되었는지 확인
        System.out.println("-------------------- user1 삭제 조회 --------------------");
        System.out.println(userService.findById(user1.getId()));


        System.out.println();


        // Channel 구현 테스트
        Channel ch1 = new Channel("game1");
        channelService.create(ch1);

        Channel ch2 = new Channel("game2");
        channelService.create(ch2);

        System.out.println("----------------------- ch1 조회 -----------------------");
        System.out.println(channelService.findById(ch1.getId()));

        System.out.println("----------------------- 모든 channel 조회 -----------------------");
        System.out.println(channelService.findAll());

        // ch1 수정
        ch1.update("change game1");
        System.out.println("----------------------- 수정된 ch1 조회 -----------------------");
        System.out.println(channelService.findById(ch1.getId()));

        // ch1 삭제
        channelService.delete(ch1.getId());
        System.out.println("----------------------- ch1 삭제 조회 -----------------------");
        System.out.println(channelService.findById(ch1.getId()));

        System.out.println();

        // Message 구현 테스트
        Message msg1 = new Message("msg1", ch2.getId(), user2.getId());
        messageService.create(msg1);

        Message msg2 = new Message("msg2", ch2.getId(), user3.getId());
        messageService.create(msg2);

        System.out.println("----------------------- msg1 조회 -----------------------");
        System.out.println(messageService.findById(msg1.getId()));

        System.out.println("----------------------- 모든 msg 조회 -----------------------");
        System.out.println(messageService.findAll());

        // msg1 수정
        msg1.update("hi");
        System.out.println("----------------------- 수정된 msg1 조회 -----------------------");
        System.out.println(messageService.findById(msg1.getId()));

        // msg1 삭제
        messageService.delete(msg1.getId());
        System.out.println("----------------------- msg1 삭제 조회 -----------------------");
        System.out.println(messageService.findById(msg1.getId()));

        System.out.println();


        // 심화 요구 사항 테스트
        System.out.println("======================= 심화 테스트 =======================");
        System.out.println("---------------------- 성공 ----------------------");
        Message vMsg = new Message("valid msg", ch2.getId(), user2.getId());
        Message result1 = messageService.create(vMsg);

        if (result1 == null) {
            System.out.println("메세지 생성 실패");
        } else {
            System.out.println("메세지 생성내용: " + result1);
        }

        System.out.println("---------------------- 실패 ----------------------");
        Message vMsg2 = new Message("valid msg", ch2.getId(), user1.getId()); // 삭제된 user1
        Message result2 = messageService.create(vMsg2);

        if (result2 == null) {
            System.out.println("메세지 생성 실패");
        } else {
            System.out.println("메세지 생성내용: " + result2);
        }

    }
}