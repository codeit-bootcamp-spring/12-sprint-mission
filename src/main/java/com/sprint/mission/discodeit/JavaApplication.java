package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {
    public static void main(String[] args) {
        // JCF Repository 테스트
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();

        // File Repository 테스트
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(userService, channelService, messageRepository);

        // 사용자 테스트 시작
        System.out.println("-----------------사용자 테스트 시작-----------------");

        // 사용자 저장
        User user1 = new User("test1", "test1@example.com", "1234", "user1");
        userService.save(user1);
        User user2 = new User("test2", "test2@email.com", "2345", "user2");
        userService.save(user2);

        // 사용자 전체 조회
        System.out.println("===== 사용자 전체 조회 =====");
        System.out.println(userService.findAll());

        // 사용자 단건 조회
        System.out.println("===== user1 조회 =====");
        System.out.println(userService.findById(user1.getId()));

        // 사용자 정보 수정
        System.out.println("===== user1 정보 수정 =====");
        userService.update(user1.getId(), "update1", "update1@example.com", "1111", "hihi1");
        System.out.println(userService.findById(user1.getId()));

        // 사용자 삭제
        System.out.println("===== user2 삭제 =====");
        System.out.println("삭제 전 사용자 수: " + userService.findAll().size());
        userService.delete(user2.getId());
        System.out.println("삭제 후 사용자 수: " + userService.findAll().size());

        // 사용자 테스트 종료
        System.out.println("-----------------사용자 테스트 종료-----------------");
        System.out.println();

        // 채널 테스트 시작
        System.out.println("-----------------채널 테스트 시작-----------------");

        // 채널 저장 및 전체 조회
        System.out.println("===== 채널 저장 및 전체 조회 =====");
        Channel channel = new Channel(ChannelType.PRIVATE, "공지방");
        channelService.save(channel);
        System.out.println(channelService.findAll());

        // 채널 수정 및 조회
        System.out.println("===== 채널 수정 및 단건 조회 =====");
        channelService.update(channel.getId(), "★공지방★");
        System.out.println(channelService.findById(channel.getId()));

        // 채널 테스트 종료
        System.out.println("-----------------채널 테스트 종료-----------------");
        System.out.println();

        // 메시지 테스트 시작
        System.out.println("-----------------메시지 테스트 시작-----------------");

        // 메시지 저장
        System.out.println("===== 메시지 저장 =====");
        Message message = new Message(user1.getId(), channel.getId(), "안녕하세요!");
        messageService.save(message);
        // 존재하지 않는 사용자 메시지 저장
//        Message messageNotUser = new Message(user2.getId(), channel.getId(), "안뇽");
//        messageService.save(messageNotUser);

        // 메시지 수정 및 조회
        System.out.println("===== 메시지 수정 및 단건 조회 =====");
        messageService.update(message.getId(), "안녕하세요~!");
        System.out.println(messageService.findById(message.getId()));

        // 메시지 삭제
        System.out.println("===== 메시지 삭제 =====");
        System.out.println("삭제 전 메시지 수: " + messageService.findAll().size());
        messageService.delete(message.getId());
        System.out.println("삭제 후 메시지 수: " + messageService.findAll().size());
    }
}