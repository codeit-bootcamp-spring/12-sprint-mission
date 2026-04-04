package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.file.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.UUID;
import java.util.function.Supplier;

public class JavaApplication {
    public static class ServiceTestRunner {
        private final UserService userService;
        private final ChannelService channelService;
        private final MessageService messageService;

        public ServiceTestRunner(UserService userService, ChannelService channelService, MessageService messageService) {
            this.userService = userService;
            this.channelService = channelService;
            this.messageService = messageService;
        }

        public void runAllTests() {
            createInitialData();
            displayAllData();
            updateTestData();
            deleteTestData();
        }

        // [1] 초기 데이터 생성
        private void createInitialData() {
            System.out.println("======= [1] 초기 데이터 생성 =======");
            for (int i = 1; i <= 4; i++) {
                User u = userService.save(new User("User" + i, "u" + i + "@test.com", "Nick" + i, "password" + i));
                Channel c = channelService.save(new Channel("Channel" + i, u, "Category" + i));
                messageService.save(new Message(c, u, "Title" + i, "Content" + i));
            }
            System.out.println("데이터 생성 완료.\n");
        }

        // [2] 전체 조회
        private void displayAllData() {
            System.out.println("======= [2] 모든 서비스 객체 전체 조회 =======");
            System.out.println("전체 유저: " + userService.findAll());
            System.out.println("전체 채널: " + channelService.findAll());
            System.out.println("전체 메시지: " + messageService.findAll());
            System.out.println();
        }

        // [3] 부분 수정 (Update)
        private void updateTestData() {
            System.out.println("======= [3] 서비스 update를 통한 부분 수정 =======");

            // 유저 수정
            System.out.println("--------------- User 닉네임 정보 수정 ---------------");
            User firstUser = userService.findAll().get(0);
            final UUID targetUserId = firstUser.getId();
            User userUpdateReq = new User(null, null, "★슈퍼닉네임★", null) {
                @Override
                public UUID getId() {
                    return targetUserId;
                }
            };
            userService.update(userUpdateReq);
            System.out.println("User 수정 완료: " + userService.findById(targetUserId));

            // 채널 수정
            System.out.println("--------------- Channel 제목 수정 ---------------");
            Channel secondChannel = channelService.findAll().get(1);
            final UUID targetChannelId = secondChannel.getId();
            Channel channelUpdateReq = new Channel("업데이트된_비밀채널", null, null) {
                @Override
                public UUID getId() {
                    return targetChannelId;
                }
            };
            channelService.update(channelUpdateReq);
            System.out.println("Channel 수정 완료: " + channelService.findById(targetChannelId));

            // 메시지 수정
            System.out.println("--------------- Message 내용 수정 ---------------");
            Message thirdMsg = messageService.findAll().get(2);
            final UUID targetMsgId = thirdMsg.getId();
            Message msgUpdateReq = new Message(null, null, null, "내용이 완전히 바뀌었습니다!") {
                @Override
                public UUID getId() {
                    return targetMsgId;
                }
            };
            messageService.update(msgUpdateReq);
            System.out.println("Message 수정 완료: " + messageService.findById(targetMsgId));
            System.out.println();
        }

        // [4] 데이터 삭제 (Delete)
        private void deleteTestData() {
            System.out.println("======= [4] 데이터 삭제 =======");

            UUID lastUserId = userService.findAll().get(3).getId();
            UUID lastChannelId = channelService.findAll().get(3).getId();
            UUID lastMsgId = messageService.findAll().get(3).getId();

            System.out.println("삭제 대상 ID: User(" + lastUserId + "), Channel(" + lastChannelId + "), Msg(" + lastMsgId + ")");

            userService.delete(lastUserId);
            channelService.delete(lastChannelId);
            messageService.delete(lastMsgId);

            System.out.println("\n--- 최종 삭제 결과 확인 ---");
            verifyDeletion("User", () -> userService.findById(lastUserId));
            verifyDeletion("Channel", () -> channelService.findById(lastChannelId));
            verifyDeletion("Message", () -> messageService.findById(lastMsgId));
        }

        private void verifyDeletion(String label, Supplier<?> findAction) {
            try {
                Object result = findAction.get();
                if (result == null) {
                    System.out.println(label + " 삭제 성공!");
                } else {
                    System.out.println(label + " 삭제 실패!" + result + "가 아직 존재");
                }
            } catch (Exception e) {
                System.out.println("삭제 성공!");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("--------------- JCFU 기반 Service 테스트 ---------------");
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(channelService, userService);

        ServiceTestRunner runner = new ServiceTestRunner(userService, channelService, messageService);
        runner.runAllTests();
        System.out.println();

        System.out.println("--------------- File 기반 Service 테스트 ---------------");
        userService = new FileUserService();
        channelService = new FileChannelService(userService);
        messageService = new FileMessageService(channelService, userService);

        runner = new ServiceTestRunner(userService, channelService, messageService);
        runner.runAllTests();
        System.out.println();

        System.out.println("--------------- JCFU 기반 Repository 테스트 ---------------");
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        userService = new BasicUserService(userRepository);
        channelService = new BasicChannelService(channelRepository, userService);
        messageService = new BasicMessageService(messageRepository, userService, channelService);

        runner = new ServiceTestRunner(userService, channelService, messageService);
        runner.runAllTests();
        System.out.println();

        System.out.println("--------------- File 기반 Repository 테스트 ---------------");
        userRepository = new FileUserRepository();
        channelRepository = new FileChannelRepository();
        messageRepository = new FileMessageRepository();

        userService = new BasicUserService(userRepository);
        channelService = new BasicChannelService(channelRepository, userService);
        messageService = new BasicMessageService(messageRepository, userService, channelService);

        runner = new ServiceTestRunner(userService, channelService, messageService);
        runner.runAllTests();
        System.out.println();
    }
}