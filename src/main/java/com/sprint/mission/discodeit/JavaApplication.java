package com.sprint.mission.discodeit;

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
        MessageService messageService = new JCFMessageService();

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
        System.out.println(userService.findAll());



    }
}