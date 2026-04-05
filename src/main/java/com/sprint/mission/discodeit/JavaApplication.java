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

                // 서비스 객체 생성
                UserService userService = new JCFUserService();

                // 등록 (Create) user1, user2
                User user1 = new User("user1", "user1@email.com", "1234", "닉네임1");
                userService.create(user1);
                User user2 = new User("user2", "user2@email.com", "5678", "닉네임2");
                userService.create(user2);

                System.out.println("=== 등록 완료 ===");
                System.out.println(user1);
                System.out.println(user2);

                // (단건 조회)
                User foundUser = userService.findById(user1.getId());
                System.out.println("=== 단건 조회 ===");
                System.out.println(foundUser);

                // 조회 (전체 조회)
                System.out.println("=== 전체 조회 ===");
                for (User u : userService.findAll()) {
                        System.out.println(u);
                }

                // 수정 user1 -> user111
                user1.update("user111", "user111@email.com", "9999", "user111");

                System.out.println("=== 수정 후 조회 ===");
                System.out.println(userService.findById(user1.getId()));

                // 삭제
                userService.delete(user1.getId());

                System.out.println("=== 삭제 후 전체 조회 ===");
                for (User u : userService.findAll()) {
                        System.out.println(u);
                }

                // 채널 + 메시지 테스트
                ChannelService channelService = new JCFChannelService();
                MessageService messageService = new JCFMessageService(userService, channelService);

                Channel channel = new Channel("일반 채널", "테스트 채널");
                channel.addUser(user2);
                channelService.create(channel);

                System.out.println("=== 채널 생성 ===");
                System.out.println(channel);

                Message message = new Message("안녕", user2, channel);
                messageService.create(message);

                System.out.println("=== 메시지 전체 조회 ===");
                for (Message m : messageService.findAll()) {
                        System.out.println(m);
                }

                messageService.delete(message.getId());

                System.out.println("=== 메시지 삭제 후 ===");
                if (messageService.findAll().isEmpty()) {
                        System.out.println("메시지가 없습니다.");
                } else {
                        for (Message m : messageService.findAll()) {
                                System.out.println(m);
                        }
                }
        }
}