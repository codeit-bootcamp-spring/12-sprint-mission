package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {

    static User setupUser(UserService userService) {
        return userService.createUser("woody", "woody@codeit.com", "woody1234", "우디");
    }
    //채널 타입을 미구현하여 코드 템플릿을 일부 수정
    static Channel setupChannel(ChannelService channelService) {
        return channelService.createChannel("공지");
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.sendMessage(channel.getId(), author.getId(), "안녕하세요.");
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

        // =========================================================
        // Repository 초기화 (원하는 저장 방식의 주석을 풀어 사용)
        // =========================================================

         //[메모리 저장 방식 - JCF]
         UserRepository userRepository = new JCFUserRepository();
         ChannelRepository channelRepository = new JCFChannelRepository();
         MessageRepository messageRepository = new JCFMessageRepository();

        // [하드디스크 저장 방식 - File]
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();


        // =========================================================
        // 2. Service 초기화 (의존성 주입)
        // =========================================================
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);


        System.out.println("--- [User 도메인 테스트] ---");

        // 등록
        User user1 = userService.createUser("user1", "user1@test.com", "pass1", "홍길동");
        User user2 = userService.createUser("user2", "user2@test.com", "pass2", "김길동");
        System.out.println("[등록] User1 등록 완료: " + user1.getNickname());

        // 다건 조회
        System.out.println("[다건 조회] 전체 유저 수: " + userService.getAllUsers().size() + "명");

        // 단건 조회
        User foundUser = userService.getUser(user1.getId());
        System.out.println("[단건 조회] 조회된 유저 닉네임: " + foundUser.getNickname());

        // 수정
        userService.updateUser(user1.getId(), "user1_updated", "user1@test.com", "newpass", "박길동");

        // 수정된 데이터 조회
        User updatedUser = userService.getUser(user1.getId());
        System.out.println("[수정 확인] 변경된 닉네임: " + updatedUser.getNickname());

        // 삭제
        userService.deleteUser(user2.getId());

        // 조회를 통해 삭제되었는지 확인
        User deletedUser = userService.getUser(user2.getId());
        System.out.println("[삭제 확인] 삭제된 유저 조회 결과 (null 예상): " + deletedUser);

        System.out.println("\n--- [Channel 도메인 테스트] ---");

        // 등록
        Channel channel1 = channelService.createChannel("개발-자료");
        Channel channel2 = channelService.createChannel("위클리_페이퍼");
        System.out.println("[등록] Channel1 등록 완료: " + channel1.getName());

        // 다건 조회
        System.out.println("[다건 조회] 전체 채널 수: " + channelService.getAllChannels().size() + "개");

        // 단건 조회
        Channel foundChannel = channelService.getChannel(channel1.getId());
        System.out.println("[단건 조회] 조회된 채널명: " + foundChannel.getName());

        // 수정
        channelService.updateChannel(channel1.getId(), "새발-자료");

        // 수정된 데이터 조회
        Channel updatedChannel = channelService.getChannel(channel1.getId());
        System.out.println("[수정 확인] 변경된 채널명: " + updatedChannel.getName());

        // 삭제
        channelService.deleteChannel(channel2.getId());

        // 조회를 통해 삭제되었는지 확인
        Channel deletedChannel = channelService.getChannel(channel2.getId());
        System.out.println("[삭제 확인] 삭제된 채널 조회 결과 (null 예상): " + deletedChannel);


        System.out.println("\n--- [Message 도메인 테스트] ---");

        // 등록 (User1이 Channel1에 메시지 작성)
        Message msg1 = messageService.sendMessage(channel1.getId(), user1.getId(), "메세지 테스트 1");
        Message msg2 = messageService.sendMessage(channel1.getId(), user1.getId(), "메세지 테스트 2");
        System.out.println("[등록] Message1 등록 완료: " + msg1.getContent());

        // 다건 조회 (특정 채널의 메시지 목록)
        System.out.println("[다건 조회] 채널1의 메시지 수: " + messageService.getMessagesByChannelId(channel1.getId()).size() + "개");

        // 단건 조회
        Message foundMsg = messageService.getMessage(msg1.getId());
        System.out.println("[단건 조회] 조회된 메시지 내용: " + foundMsg.getContent());

        // 수정
        messageService.updateMessage(msg1.getId(), "수정 메세지 테스트 1");

        // 수정된 데이터 조회
        Message updatedMsg = messageService.getMessage(msg1.getId());
        System.out.println("[수정 확인] 변경된 메시지 내용: " + updatedMsg.getContent());

        // 삭제
        messageService.deleteMessage(msg2.getId());

        // 조회를 통해 삭제되었는지 확인
        Message deletedMsg = messageService.getMessage(msg2.getId());
        System.out.println("[삭제 확인] 삭제된 메시지 조회 결과 (null 예상): " + deletedMsg);

        System.out.println("\n========== 테스트 정상 종료 ==========");
    }
}