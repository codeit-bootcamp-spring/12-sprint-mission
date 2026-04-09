package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
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
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.NoSuchElementException;

public class JavaApplication {

    static User setupUser(UserService userService) {
        User user = new User("woody", "woody@codeit.com", "woody1234", "woody");
        return userService.create(user);
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = new Channel("공지");
        return channelService.create(channel);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = new Message("안녕하세요.", channel.getId(), author.getId());
        Message created = messageService.create(message);
        System.out.println("메시지 생성: " + created.getId());
    }

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

        try {
            Message result1 = messageService.create(vMsg);
            System.out.println("메세지 생성내용: " + result1);
        } catch (NoSuchElementException e) {
            System.out.println("메세지 생성 실패: " + e.getMessage());
        }

        System.out.println("---------------------- 실패 ----------------------");
        Message vMsg2 = new Message("valid msg", ch2.getId(), user1.getId()); // 삭제된 user1

        try {
            Message result2 = messageService.create(vMsg2);
            System.out.println("메세지 생성내용: " + result2);
        } catch (NoSuchElementException e) {
            System.out.println("메세지 생성 실패: " + e.getMessage());
        }


        System.out.println();
        System.out.println("==================================================");
        System.out.println("============== FileService 구현 테스트 ==============");
        System.out.println("==================================================");

        UserService fileUserService = new FileUserService();
        ChannelService fileChannelService = new FileChannelService();
        MessageService fileMessageService = new FileMessageService(fileChannelService, fileUserService);

        // User Service 구현 테스트
        User fUser1 = new User("fileUser1", "fileUser1@email.com", "1111", "fileNick1");
        fileUserService.create(fUser1);

        User fUser2 = new User("fileUser2", "fileUser2@email.com", "2222", "fileNick2");
        fileUserService.create(fUser2);

        System.out.println("----------------------- file user1 조회 -----------------------");
        System.out.println(fileUserService.findById(fUser1.getId()));

        System.out.println("----------------------- 모든 File User 조회 -----------------------");
        System.out.println(fileUserService.findAll());

        fUser1.update("fileUser12", "fileUser12@email.com", "9999", "fileNick12");
        fileUserService.update(fUser1.getId(), fUser1);

        System.out.println("----------------------- 수정된 file user1 조회 -----------------------");
        System.out.println(fileUserService.findById(fUser1.getId()));

        fileUserService.delete(fUser1.getId());
        System.out.println("----------------------- 삭제된 file user1 조회 -----------------------");
        System.out.println(fileUserService.findById(fUser1.getId()));

        System.out.println();

        // Channel Service 구현 테스트
        Channel fCh1 = new Channel("file channel 1");
        fileChannelService.create(fCh1);

        Channel fCh2 = new Channel("file channel 2");
        fileChannelService.create(fCh2);

        System.out.println("----------------------- file channel1 조회 -----------------------");
        System.out.println(fileChannelService.findById(fCh1.getId()));

        System.out.println("----------------------- 모든 File Channel 조회 -----------------------");
        System.out.println(fileChannelService.findAll());

        fCh1.update("file-channel-12");
        fileChannelService.update(fCh1);

        System.out.println("----------------------- 수정된 file channel1 조회 -----------------------");
        System.out.println(fileChannelService.findById(fCh1.getId()));

        fileChannelService.delete(fCh1.getId());
        System.out.println("----------------------- 삭제된 file channel1 조회 -----------------------");
        System.out.println(fileChannelService.findById(fCh1.getId()));

        System.out.println();

        // Message Service 구현 테스트
        Message fMsg1 = new Message("file message1", fCh2.getId(), fUser2.getId());
        fileMessageService.create(fMsg1);

        Message fMsg2 = new Message("file message2", fCh2.getId(), fUser2.getId());
        fileMessageService.create(fMsg2);

        System.out.println("----------------------- file msg1 조회 -----------------------");
        System.out.println(fileMessageService.findById(fMsg1.getId()));

        System.out.println("----------------------- 모든 File Message 조회 -----------------------");
        System.out.println(fileMessageService.findAll());

        fMsg1.update("file message1 123");
        fileMessageService.update(fMsg1);

        System.out.println("----------------------- 수정된 file msg1 조회 -----------------------");
        System.out.println(fileMessageService.findById(fMsg1.getId()));

        fileMessageService.delete(fMsg1.getId());
        System.out.println("----------------------- 삭제된 file msg1 조회 -----------------------");
        System.out.println(fileMessageService.findById(fMsg1.getId()));

        System.out.println();

        // 심화 요구 사항 테스트
        System.out.println("======================= File 심화 테스트 =======================");
        System.out.println("---------------------- 성공 ----------------------");
        Message fileValidMsg = new Message("file valid msg", fCh2.getId(), fUser2.getId());

        try {
            Message fileResult1 = fileMessageService.create(fileValidMsg);
            System.out.println("메세지 생성내용: " + fileResult1);
        } catch (NoSuchElementException e) {
            System.out.println("메세지 생성 실패: " + e.getMessage());
        }

        System.out.println("---------------------- 실패 ----------------------");
        Message fileInvalidMsg = new Message("file invalid msg", fCh2.getId(), fUser1.getId()); // 삭제된 user

        try {
            Message fileResult2 = fileMessageService.create(fileInvalidMsg);
            System.out.println("메세지 생성내용: " + fileResult2);
        } catch (NoSuchElementException e) {
            System.out.println("메세지 생성 실패: " + e.getMessage());
        }

        System.out.println();

        // JCFRepository 테스트
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        UserService userBService = new BasicUserService(userRepository);
        ChannelService channelBService = new BasicChannelService(channelRepository);
        MessageService messageBService = new BasicMessageService(
                messageRepository,
                channelBService,
                userBService
        );

        User juser = setupUser(userBService);
        Channel jchannel = setupChannel(channelBService);

        System.out.println("---------- JCF Repository message TEST ----------");
        messageCreateTest(messageBService, jchannel, juser);
        System.out.println();

        // FileRepository 테스트
        UserRepository userFRepository = new FileUserRepository();
        ChannelRepository channelFRepository = new FileChannelRepository();
        MessageRepository messageFRepository = new FileMessageRepository();

        UserService userFService = new BasicUserService(userFRepository);
        ChannelService channelFService = new BasicChannelService(channelFRepository);
        MessageService messageFService = new BasicMessageService(
                messageFRepository,
                channelFService,
                userFService
        );

        User fuser = setupUser(userFService);
        Channel fchannel = setupChannel(channelFService);

        System.out.println("---------- FileRepository Message TEST----------");
        messageCreateTest(messageFService, fchannel, fuser);
    }
}