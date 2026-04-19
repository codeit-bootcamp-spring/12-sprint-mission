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
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        User user1 = new User("test.com", "1234", "kim", "kk", UUID.randomUUID());
        User user2 = new User("test2.com", "1234", "lee", "ll", UUID.randomUUID());

        Channel textChannel = new Channel("텍스트 채널", ChannelType.TEXT , true);
        Channel voiceChannel = new Channel("음성 채널", ChannelType.VOICE, false);

        Message message1 = new Message(textChannel.getId(), user1.getId(), "메세지 테스트 1", null);
        Message message2 = new Message(textChannel.getId(), user2.getId(), "메세지 테스트 2", null);

        System.out.println("====JCF SERVICE 시작====\n");
        testJCFService(user1, user2, textChannel, voiceChannel, message1, message2);
        System.out.println("\n====JCF SERVICE 끝====");

        System.out.println("====FILE SERVICE 시작====\n");
        testFileService(user1, user2, textChannel, voiceChannel, message1, message2);
        System.out.println("\n====FILE SERVICE 끝====");


    }
    private static void testJCFService(User user1, User user2, Channel channel1, Channel channel2, Message message1, Message message2) {
        System.out.println("========= JCF 세팅 시작 =========");
        ChannelRepository channelRepository = new JCFChannelRepository();
        UserRepository userRepository = new JCFUserRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        ChannelService channelService = new BasicChannelService(channelRepository);
        UserService userService = new BasicUserService(userRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);

        System.out.println("========= JCF 초기 세팅 완료 =========\n");

        testUser(user1, user2, userService);
        testChannel(channel1, channel2, channelService);
        testMessage(message1, message2, messageService);

    }

    private static void testFileService(User user1, User user2, Channel channel1, Channel channel2, Message message1, Message message2) {
        System.out.println("========= FILE 세팅 시작 =========");
        ChannelRepository channelRepository = new FileChannelRepository();
        UserRepository userRepository = new FileUserRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        ChannelService channelService = new BasicChannelService(channelRepository);
        UserService userService = new BasicUserService(userRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);

        System.out.println("========= FILE 초기 세팅 완료 =========\n");

        testUser(user1, user2, userService);
        testChannel(channel1, channel2, channelService);
        testMessage(message1, message2, messageService);

    }

    private static void testUser(User user1, User user2, UserService userService){
        System.out.println("====User save====");
        userService.create(user1);
        userService.create(user2);

        System.out.println("\n====User findById====");
        System.out.println(userService.findById(user1.getId()));
        System.out.println(userService.findById(UUID.randomUUID()));

        System.out.println("\n====User findAll====");
        userService.findAll().forEach(user -> System.out.println(user));

        System.out.println("\n====User update====");
        User changeUser = new User("바뀐 이름", "바뀐 비밀번호", "바뀐 이메일", "바뀐 닉네임", UUID.randomUUID());
                //String userName, String password, String email, String nickName
        System.out.println(userService.update(user1.getId(), changeUser));
        System.out.println("====변경 완료!====");
        userService.findAll().forEach(user -> System.out.println(user));

        System.out.println("====\nUser delete====");
        userService.delete(user2.getId());
        System.out.println("====삭제 완료!====");
        userService.findAll().forEach(user -> System.out.println(user));

    }

    private static void testMessage(Message msg1, Message msg2, MessageService messageService){
        System.out.println("====Message save====");
        messageService.create(msg1);
        messageService.create(msg2);

        System.out.println("\n====Message findById====");
        System.out.println(messageService.findById(msg1.getId()));
        System.out.println(messageService.findById(UUID.randomUUID()));

        System.out.println("\n====Message findAll====");
        messageService.findAll().forEach(msg -> System.out.println(msg));

        System.out.println("\n====Message update====");
        Message changeMessage = new Message(msg1.getId(), msg1.getMemberId(), "바뀐 메세지", null);
                //UUID channelId, UUID memberId, String content
        System.out.println(messageService.update(msg1.getId(), changeMessage, null));
        System.out.println("====변경 완료!====");
        messageService.findAll().forEach(msg -> System.out.println(msg));

        System.out.println("====\nMessage delete====");
        messageService.delete(msg2.getId());
        System.out.println("====삭제 완료!====");
        messageService.findAll().forEach(msg -> System.out.println(msg));

    }

    private static void testChannel(Channel channel1, Channel channel2, ChannelService channelService){
        System.out.println("====Channel save====");
        channelService.create(channel1);
        channelService.create(channel2);

        System.out.println("\n====Channel findById====");
        System.out.println(channelService.findById(channel1.getId()));
        System.out.println(channelService.findById(UUID.randomUUID()));

        System.out.println("\n====Channel findAll====");
        channelService.findAll().forEach(channel -> System.out.println(channel));

        System.out.println("\n====Channel update====");
        Channel changeChannel = new Channel("바뀐 이름", ChannelType.FORUM, false);
        //String name, ChannelType type, boolean isPrivate
        System.out.println(channelService.update(channel1.getId(), changeChannel));
        System.out.println("====변경 완료!====");
        channelService.findAll().forEach(channel -> System.out.println(channel));

        System.out.println("====\nChannel delete====");
        channelService.delete(channel2.getId());
        System.out.println("====삭제 완료!====");
        channelService.findAll().forEach(channel -> System.out.println(channel));
    }
}
