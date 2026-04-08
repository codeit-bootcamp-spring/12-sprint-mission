package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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

import java.util.NoSuchElementException;

public class JavaApplication {

    static User setupUser(UserService userService) {
        return userService.create("woody", "woody@codeit.com", "woody1234");
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);

        System.out.println("--- [User 도메인 테스트] ---");

        User user1 = userService.create("user1", "user1@test.com", "pass1");
        User user2 = userService.create("user2", "user2@test.com", "pass2");
        System.out.println("[등록] User1 등록 완료: " + user1.getUsername());

        System.out.println("[다건 조회] 전체 유저 수: " + userService.findAll().size() + "명");

        User foundUser = userService.find(user1.getId());
        System.out.println("[단건 조회] 조회된 유저 닉네임 : " + foundUser.getUsername());

        userService.update(user1.getId(), "user1_updated", "user1@test.com", "newpass");

        User updatedUser = userService.find(user1.getId());
        System.out.println("[수정 확인] 변경된 닉네임: " + updatedUser.getUsername());

        userService.delete(user2.getId());

        try {
            userService.find(user2.getId());
        } catch (NoSuchElementException e) {
            System.out.println("[삭제 확인] 삭제된 유저 조회 결과 (예외 발생 확인): " + e.getMessage());
        }

        System.out.println("\n--- [Channel 도메인 테스트] ---");

        Channel channel1 = channelService.create(ChannelType.PUBLIC, "개발-자료", "개발 관련 자료 채널");
        Channel channel2 = channelService.create(ChannelType.PRIVATE, "위클리_페이퍼", "위클리 페이퍼 채널");
        System.out.println("[등록] Channel1 등록 완료: " + channel1.getName());

        System.out.println("[다건 조회] 전체 채널 수: " + channelService.findAll().size() + "개");

        Channel foundChannel = channelService.find(channel1.getId());
        System.out.println("[단건 조회] 조회된 채널명: " + foundChannel.getName());

        channelService.update(channel1.getId(), ChannelType.PUBLIC, "새발-자료", "설명 변경 확인");

        Channel updatedChannel = channelService.find(channel1.getId());
        System.out.println("[수정 확인] 변경된 채널명: " + updatedChannel.getName());

        channelService.delete(channel2.getId());

        try {
            channelService.find(channel2.getId());
        } catch (NoSuchElementException e) {
            System.out.println("[삭제 확인] 삭제된 채널 조회 결과 (예외 발생 확인): " + e.getMessage());
        }

        System.out.println("\n--- [Message 도메인 테스트] ---");

        Message msg1 = messageService.create("메세지 테스트 1", channel1.getId(), user1.getId());
        Message msg2 = messageService.create("메세지 테스트 2", channel1.getId(), user1.getId());
        System.out.println("[등록] Message1 등록 완료: " + msg1.getContent());

        System.out.println("[다건 조회] 전체 메시지 수: " + messageService.findAll().size() + "개");

        Message foundMsg = messageService.find(msg1.getId());
        System.out.println("[단건 조회] 조회된 메시지 내용: " + foundMsg.getContent());

        messageService.update(msg1.getId(), "수정 메세지 테스트 1");

        Message updatedMsg = messageService.find(msg1.getId());
        System.out.println("[수정 확인] 변경된 메시지 내용: " + updatedMsg.getContent());

        messageService.delete(msg2.getId());

        try {
            messageService.find(msg2.getId());
        } catch (NoSuchElementException e) {
            System.out.println("[삭제 확인] 삭제된 메시지 조회 결과 (예외 발생 확인): " + e.getMessage());
        }

        System.out.println("\n========== 테스트 정상 종료 ==========");
    }
}