package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

public class JavaApplication {

    static User setupUser(UserService userService) {
        User user = new User("woody", "woody@codeit.com", "woody1234", "jason");
        userService.create(user);
        return user;
    }

    static Channel setupChannel(ChannelService channelService, User owner) {
        Channel channel = new Channel("공지", "공지 채널입니다.", owner);
        channelService.create(channel);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = new Message("안녕하세요", author);
        messageService.create(message);
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {


        System.out.println("\n---------------오브젝트파일 초기화 시작------------------");
        Path targetPath = Paths.get("./persistentfiles");
        try {
            if (Files.exists(targetPath)) {
                try (Stream<Path> walk = Files.walk(targetPath)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("----------------오브젝트파일 초기화 끝------------------\n\n\n");

//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService();
//        UserService userService = new FileUserService();
//        ChannelService channelService = new FileChannelService();
//        MessageService messageService = new FileMessageService();
        UserService userService = new BasicUserService();
        ChannelService channelService = new BasicChannelService();
        MessageService messageService = new BasicMessageService();

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService, user);
        // 테스트
        messageCreateTest(messageService, channel, user);

        System.out.println("----------------사용자 테스트 시작!!!------------------");
        System.out.println();
        // CREATE
        System.out.println("- UserCreate \t\t: .create(user1)");
        User user1 = new User("user1", "user1@email.com", "1234", "test1");
        userService.create(user1);
        System.out.println("- UserCreate \t\t: .create(user2)");
        User user2 = new User("user2", "user2@email.com", "5678", "test2");
        userService.create(user2);
        System.out.println("- UserCreate \t\t: .create(user3)");
        User user3 = new User("user3", "user3@email.com", "9101", "test3");
        userService.create(user3);
        System.out.println("- UserCreate \t\t: .create(user4)");
        User user4 = new User("user4", "user4@email.com", "1121", "test4");
        userService.create(user4);
        System.out.println("- UserCreate \t\t: .create(user5)");
        User user5 = new User("user5", "user5@email.com", "3141", "test5");
        userService.create(user5);
        System.out.println("- UserCreate \t\t: .create(user6)");
        User user6 = new User("user6", "user6@email.com", "5161", "test6");
        userService.create(user6);
        System.out.println();
        // READ
        System.out.println("- UserRead \t\t\t: .findById(user3.getId())");
        userService.findById(user3.getId()).ifPresent(it -> System.out.println("\t" + it));
        System.out.println("- UserRead \t\t\t: .findAll()");
        userService.findAll().ifPresent(list -> list.forEach(it -> System.out.println("\t" + it)));
        System.out.println();
        // UPDATE
        System.out.println("- UserUpdate \t\t: .updateById(user4.getId())");
        userService.updateById(user4.getId(), "test4_updated", user4.getEmail(), user4.getPassword(), user4.getNickname());
        System.out.println("- UserRead \t\t\t: .findById(user4.getId())");
        userService.findById(user4.getId()).ifPresent(it -> System.out.println("\t" + it));
        System.out.println();
        // DELETE
        System.out.println("- UserDelete \t\t: .deleteById(user6.getId())");
        userService.deleteById(user6.getId());
        System.out.println("- UserRead \t\t\t: .findById(user6.getId())");
        userService.findById(user6.getId()).ifPresentOrElse(
                it -> System.out.println("\t" + it),
                () -> System.out.println("\tfindById 결과 없음(null)")
        );
        System.out.println("----------------사용자 테스트 끝!!!--------------------\n\n\n");



        System.out.println("----------------채널 테스트 시작!!!------------------");
        System.out.println();
        // CREATE
        System.out.println("- ChannelCreate \t: .create(channel1)");
        Channel channel1 = new Channel("channel1", "thisischannel1", user1);
        channelService.create(channel1);
        System.out.println("- ChannelCreate \t: .create(channel2)");
        Channel channel2 = new Channel("channel2", "thisischannel2", user3);
        channelService.create(channel2);
        System.out.println("- ChannelCreate \t: .create(channel3)");
        Channel channel3 = new Channel("channel3", "thisischannel3", user5);
        channelService.create(channel3);
        System.out.println();
        // READ
        System.out.println("- ChannelRead \t\t: .findById(channel1.getId())");
        channelService.findById(channel1.getId()).ifPresent(it -> System.out.println("\t" + it));
        System.out.println("- ChannelRead \t\t: .findAll()");
        channelService.findAll().ifPresent(list -> list.forEach(it -> System.out.println("\t" + it)));
        System.out.println();
        // UPDATE
        System.out.println("- ChannelUpdate \t: .updateById(channel3.getId())");
        channelService.updateById(channel3.getId(), "channel3_updated", channel3.getDescription(), channel3.getOwner());
        System.out.println("- ChannelRead \t\t: .findById(channel3.getId()");
        channelService.findById(channel3.getId()).ifPresent(it -> System.out.println("\t" + it));
        System.out.println("- ChannelUpdate \t: .addUser(channel1.getId(), user2)");
        channelService.addUser(channel1.getId(), user2);
        System.out.println("- ChannelUpdate \t: .addUser(channel2.getId(), user4)");
        channelService.addUser(channel2.getId(), user4);
        System.out.println("- ChannelUpdate \t: .deleteUser(channel2.getId(), user4)");
        channelService.deleteUser(channel2.getId(), user4);
        System.out.println("- ChannelUpdate \t: .deleteUser(channel3.getId(), user5)");
        System.out.println();
        // DELETE
        System.out.println("- ChannelDelete \t: .deleteById(channel3.getId()");
        channelService.deleteById(channel3.getId());
        System.out.println("- ChannelRead \t\t: .findById(channel3.getId()");
        channelService.findById(channel3.getId()).ifPresentOrElse(
                it -> System.out.println("\t" + it),
                () -> System.out.println("\tfindById 결과 없음(null)")
        );
        System.out.println("----------------채널 테스트 끝!!!--------------------\n\n\n");



        System.out.println("----------------메시지 테스트 시작!!!-------------------");
        System.out.println();
        // CREATE
        System.out.println("- MessageCreate \t: .create(message1)");
        Message message1 = new Message("thisismessage1", user1);
        messageService.create(message1);
        System.out.println("- ChannelUpdate \t: .addMessage(channel1.getId(), message1)");
        channelService.addMessage(channel1.getId(), message1);
        System.out.println("- MessageCreate \t: .create(message2)");
        Message message2 = new Message("thisismessage2", user2);
        messageService.create(message2);
        System.out.println("- ChannelUpdate \t: .addMessage(channel1.getId(), message2)");
        channelService.addMessage(channel1.getId(), message2);
        System.out.println("- MessageCreate \t: .create(message3)");
        Message message3 = new Message("thisismessage3", user3);
        messageService.create(message3);
        System.out.println("- ChannelUpdate \t: .addMessage(channel2.getId(), message3)");
        channelService.addMessage(channel2.getId(), message3);
        System.out.println();
        // READ
        System.out.println("- MessageRead\t\t: .findById(message1.getId())");
        messageService.findById(message1.getId()).ifPresent(it -> System.out.println("\t"+ it));
        System.out.println("- MessageRead\t\t: .findBySendUser(user1)");
        messageService.findBySendUser(user1).ifPresent(list -> list.forEach(it -> System.out.println("\t" + it)));
        System.out.println("- MessageRead\t\t: .findAll()");
        messageService.findAll().ifPresent(list -> list.forEach(it -> System.out.println("\t" + it)));
        System.out.println();
        // UPDATE
        System.out.println("- MessageUpdate\t\t: .updateById(message3.getId())");
        messageService.updateById(message3.getId(), "thisismessage3+updated");
        System.out.println("- MessageRead\t\t: .findById(message3.getId())");
        messageService.findById(message3.getId()).ifPresent(it -> System.out.println("\t"+ it));;
        System.out.println();
        // DELETE
        System.out.println("- MessageDelete\t\t: .deleteById(message2.getId())");
        messageService.deleteById(message2.getId());
        System.out.println("- ChannelUpdate \t: .deleteMessage(channel1.getId(), message2)");
        channelService.deleteMessage(channel1.getId(), message2);
        System.out.println("- MessageRead\t\t: .findById(message2.getId())");
        messageService.findById(message2.getId()).ifPresentOrElse(
                it -> System.out.println("\t" + it),
                () -> System.out.println("\tfindById 결과 없음(null)")
        );
        System.out.println("----------------메시지 테스트 끝!!!---------------------\n\n\n");



        System.out.println("----------------의존성 테스트 시작!!!------------------");
        System.out.println();
        // CREATE
        System.out.println("- UserCreate \t\t: 의존성 없음");
        System.out.println("- ChannelCreate\t\t: User(owner) 필요");
        System.out.println("\tUserCreate : .create(user7)");
        User user7 = new User("user7", "user7@email.com", "7181", "test7");
        userService.create(user7);
        System.out.println("\tUserCreate : .create(user8)");
        User user8 = new User("user8", "user8@email.com", "9202", "test8");
        userService.create(user8);
        System.out.println("\tChannelCreate : .create(channel4)");
        Channel channel4 = new Channel("channel4", "thisischannel4", user7);
        channelService.create(channel4);
        System.out.println("- MessageCreate\t\t: User(sendUser), ChannelService.addMessage() 필요");
        System.out.println("\tMessageCreate : .create(message4)");
        Message message4 = new Message("thisismessage4", user7);
        messageService.create(message4);
        System.out.println("\tChannelUpdate : .addMessage(channel4.getId(), message4)");
        channelService.addMessage(channel4.getId(), message4);
        System.out.println("\tMessageCreate : .create(message5)");
        Message message5 = new Message("thisismessage5", user7);
        messageService.create(message5);
        System.out.println("\tChannelUpdate : .addMessage(channel2.getId(), message5)");
        channelService.addMessage(channel2.getId(), message5);
        System.out.println();
        // READ
        System.out.println("- UserRead \t\t\t: 의존성 없음");
        System.out.println("- ChannelRead\t\t: 의존성 없음");
        System.out.println("- MessageRead\t\t: 의존성 없음");
        System.out.println();
        // UPDATE
        System.out.println("- UserUpdate \t\t: 의존성 없음");
        System.out.println("- ChannelUpdate\t\t: 의존성 없음");
        channelService.addUser(channel4.getId(), user8);
        System.out.println("- MessageUpdate\t\t: 의존성 없음");
        System.out.println();
        // DELETE
        System.out.println("- UserDelete \t\t: ChannelService.deleteUser() 필요");
        System.out.println("\tUserDelete : .deleteById(user7.getId())");
        userService.deleteById(user7.getId());
        System.out.println("\tChannelUpdate : .deleteUser(channel4.getId(), user7)");
        channelService.deleteUser(channel4.getId(), user7);
        System.out.println("- ChannelDelete\t\t: MessageService.deleteById() 필요");
        System.out.println("\tChnnelRead : .findById(channel4.getId())");
        System.out.println("\tMessageDelete : .deleteById(message.getId())");
        channelService.findById(channel4.getId()).ifPresent(found -> found.getMessageList().forEach(message -> messageService.deleteById(message.getId())));
        System.out.println("\tChannelDelete : .deleteById(channel4.getId())");
        channelService.deleteById(channel4.getId());
        System.out.println("- MessageDelete\t\t: ChannelService.deleteMessage() 필요");
        System.out.println("\tChannelUpdate : .deleteMessage(channel2.getId(), message3)");
        channelService.deleteMessage(channel2.getId(), message3);
        System.out.println("\tMessageDelete : .deleteById(message3.getId())");
        messageService.deleteById(message3.getId());
        System.out.println("----------------의존성 테스트 끝!!!--------------------\n");
    }
}
