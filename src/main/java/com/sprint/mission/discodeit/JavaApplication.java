package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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

import java.nio.file.Files;
import java.nio.file.Path;

public class JavaApplication {

    static User setupUser(UserService userService) {
        return userService.create("woody", "woody@codeit.com", "woody1234");
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
    }

    static Message setupMessage(MessageService messageService, Channel channel, User author) {
        return messageService.create("안녕하세요.", channel.getId(), author.getId());
    }

    static void runCommonTest(String title,
                              UserService userService,
                              ChannelService channelService,
                              MessageService messageService) {
        System.out.println("===== " + title + " 테스트 시작 =====");

        // 생성
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        Message message = setupMessage(messageService, channel, user);

        System.out.println("[생성] userId = " + user.getId());
        System.out.println("[생성] channelId = " + channel.getId());
        System.out.println("[생성] messageId = " + message.getId());

        // 단건 조회
        User foundUser = userService.findById(user.getId());
        Channel foundChannel = channelService.findById(channel.getId());
        Message foundMessage = messageService.findById(message.getId());

        System.out.println("[단건조회] user = " + foundUser);
        System.out.println("[단건조회] channel = " + foundChannel);
        System.out.println("[단건조회] message = " + foundMessage);

        // 전체 조회
        System.out.println("[전체조회] users size = " + userService.findAll().size());
        System.out.println("[전체조회] channels size = " + channelService.findAll().size());
        System.out.println("[전체조회] messages size = " + messageService.findAll().size());

        // 수정
        userService.update(user.getId(), "woody-updated", "woody2@codeit.com", "newpass1234");
        channelService.update(channel.getId(), ChannelType.PRIVATE, "공지수정", "공지 채널 설명 수정");
        messageService.update(message.getId(), "메시지 수정 완료");

        System.out.println("[수정후조회] user = " + userService.findById(user.getId()));
        System.out.println("[수정후조회] channel = " + channelService.findById(channel.getId()));
        System.out.println("[수정후조회] message = " + messageService.findById(message.getId()));

        // 삭제
        messageService.delete(message.getId());
        channelService.delete(channel.getId());
        userService.delete(user.getId());

        System.out.println("[삭제후전체조회] users size = " + userService.findAll().size());
        System.out.println("[삭제후전체조회] channels size = " + channelService.findAll().size());
        System.out.println("[삭제후전체조회] messages size = " + messageService.findAll().size());

        System.out.println("===== " + title + " 테스트 종료 =====");
        System.out.println();
    }

    static void cleanupFileData() {
        try {
            Files.deleteIfExists(Path.of("users.dat"));
            Files.deleteIfExists(Path.of("channels.dat"));
            Files.deleteIfExists(Path.of("messages.dat"));
        } catch (Exception e) {
            throw new RuntimeException("기존 파일 데이터 삭제 중 오류가 발생했습니다.", e);
        }
    }

    static void testWithJCFRepositories() {
        JCFUserRepository userRepository = new JCFUserRepository();
        JCFChannelRepository channelRepository = new JCFChannelRepository();
        JCFMessageRepository messageRepository = new JCFMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(
                messageRepository,
                channelRepository,
                userRepository
        );

        runCommonTest("JCF Repository", userService, channelService, messageService);
    }

    static void testWithFileRepositories() {
        cleanupFileData();

        FileUserRepository userRepository = new FileUserRepository();
        FileChannelRepository channelRepository = new FileChannelRepository();
        FileMessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(
                messageRepository,
                channelRepository,
                userRepository
        );

        runCommonTest("File Repository", userService, channelService, messageService);
    }

    public static void main(String[] args) {
        testWithJCFRepositories();
        testWithFileRepositories();
    }
}