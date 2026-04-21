package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootApplication
public class DiscodeitApplication {

    static User setupUser(UserService userService) {
        return userService.create(UserCreateDTO.builder().username("woody").email("woody@").password("woody1234").profileImageType(null).profileImage(null).build());
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.createPublic(ChannelCreateDTO.builder().name("공지").description("공지 채널입니다.").users(null).build());
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(MessageCreateDTO.builder().content("안녕하세요.").channelId(channel.getId()).authorId(author.getId()).attachmentTypes(null).attachments(null).build());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);

        System.out.println("----------------사용자 테스트 시작!!!------------------");
        System.out.println();
        // CREATE
        System.out.println("- UserCreate \t\t: .create(user1)");
        User user1 = userService.create(UserCreateDTO.builder().username("user1").email("user1@email.com").password("1234").profileImageType(null).profileImage(null).build());
        System.out.println("- UserCreate \t\t: .create(user2)");
        User user2 = userService.create(UserCreateDTO.builder().username("user2").email("user2@email.com").password("5678").profileImageType(null).profileImage(null).build());
        System.out.println("- UserCreate \t\t: .create(user3)");
        User user3 = userService.create(UserCreateDTO.builder().username("user3").email("user3@email.com").password("9101").profileImageType(null).profileImage(null).build());
        System.out.println("- UserCreate \t\t: .create(user4)");
        User user4 = userService.create(UserCreateDTO.builder().username("user4").email("user4@email.com").password("1121").profileImageType(null).profileImage(null).build());
        System.out.println("- UserCreate \t\t: .create(user5)");
        User user5 = userService.create(UserCreateDTO.builder().username("user5").email("user5@email.com").password("3141").profileImageType(null).profileImage(null).build());
        System.out.println("- UserCreate \t\t: .create(user6)");
        User user6 = userService.create(UserCreateDTO.builder().username("user6").email("user6@email.com").password("5161").profileImageType(null).profileImage(null).build());
        System.out.println();
        // READ
        System.out.println("- UserRead \t\t\t: .find(user3.getId())");
        System.out.println("\t" + userService.find(user3.getId()));
        System.out.println("- UserRead \t\t\t: .findAll()");
        userService.findAll().forEach(it -> System.out.println("\t" + it));
        System.out.println();
        // UPDATE
        System.out.println("- UserUpdate \t\t: .update(user4.getId())");
        userService.update(UserUpdateDTO.builder().id(user4.getId()).username("user4_updated").email(user4.getEmail()).password(user4.getPassword()).profileImageType(null).profileImage(null).build());
        System.out.println("- UserRead \t\t\t: .find(user4.getId())");
        System.out.println("\t" + userService.find(user4.getId()));
        System.out.println();
        // DELETE
        System.out.println("- UserDelete \t\t: .delete(user6.getId())");
        userService.delete(user6.getId());
        System.out.println("- UserRead \t\t\t: .find(user6.getId())");
        try {
            System.out.println("\t" + userService.find(user6.getId()));
        } catch (NoSuchElementException e) {
//            e.printStackTrace();
            System.out.println("\t\t해당 사용자를 찾을 수 없습니다");
        }
        System.out.println("----------------사용자 테스트 끝!!!--------------------\n\n\n");


        System.out.println("----------------채널 테스트 시작!!!------------------");
        System.out.println();
        // CREATE
        System.out.println("- ChannelCreate \t: .create(channel1)");
        Channel channel1 = channelService.createPublic(ChannelCreateDTO.builder().name("channel1").description("thisischannel1").users(null).build());
        System.out.println("- ChannelCreate \t: .create(channel2)");
        Channel channel2 = channelService.createPrivate(ChannelCreateDTO.builder().name("channel2").description("thisischannel2").users(List.of(user1, user2, user3)).build());
        System.out.println("- ChannelCreate \t: .create(channel3)");
        Channel channel3 = channelService.createPublic(ChannelCreateDTO.builder().name("channel3").description("thisischannel3").users(null).build());
        System.out.println();
        // READ
        System.out.println("- ChannelRead \t\t: .findById(channel1.getId())");
        System.out.println("\t" + channelService.find(channel1.getId()));
        System.out.println("- ChannelRead \t\t: .findAll()");
        channelService.findAllByUserId(user1.getId()).forEach(it -> System.out.println("\t" + it));
        System.out.println();
        // UPDATE
        System.out.println("- ChannelUpdate \t: .updateById(channel3.getId())");
        channelService.update(ChannelUpdateDTO.builder().id(channel3.getId()).name("channel3_updated").description(channel3.getDescription()).build());
        System.out.println("- ChannelRead \t\t: .findById(channel3.getId()");
        System.out.println("\t" + channelService.find(channel3.getId()));
        System.out.println("- ChannelUpdate \t: .addUser(channel1.getId(), user2)");
        System.out.println();
        // DELETE
        System.out.println("- ChannelDelete \t: .deleteById(channel3.getId()");
        channelService.delete(channel3.getId());
        System.out.println("- ChannelRead \t\t: .findById(channel3.getId()");
        try {
            System.out.println("\t" + channelService.find(channel3.getId()));
        } catch (NoSuchElementException e) {
//            e.printStackTrace();
            System.out.println("\t\t해당 채널을 찾을 수 없습니다");
        }
        System.out.println("----------------채널 테스트 끝!!!--------------------\n\n\n");


        System.out.println("----------------메시지 테스트 시작!!!-------------------");
        System.out.println();
        // CREATE
        System.out.println("- MessageCreate \t: .create(message1)");
        Message message1 = messageService.create(MessageCreateDTO.builder().content("thisismessage1").channelId(channel1.getId()).authorId(user1.getId()).attachmentTypes(null).attachments(null).build());
        System.out.println("- MessageCreate \t: .create(message2)");
        Message message2 = messageService.create(MessageCreateDTO.builder().content("thisismessage2").channelId(channel1.getId()).authorId(user2.getId()).attachmentTypes(null).attachments(null).build());
        System.out.println("- MessageCreate \t: .create(message3)");
        Message message3 = messageService.create(MessageCreateDTO.builder().content("thisismessage3").channelId(channel1.getId()).authorId(user3.getId()).attachmentTypes(null).attachments(null).build());
        System.out.println();
        // READ
        System.out.println("- MessageRead\t\t: .findById(message1.getId())");
        System.out.println("\t"+ messageService.find(message1.getId()));
        System.out.println("- MessageRead\t\t: .findAll()");
        messageService.findByChannelId(channel1.getId()).forEach(it -> System.out.println("\t" + it));
        System.out.println();
        // UPDATE
        System.out.println("- MessageUpdate\t\t: .updateById(message3.getId())");
        messageService.update(MessageUpdateDTO.builder().id(message3.getId()).content("thisismessage3+updated").attachmentTypes(null).attachments(null).build());
        System.out.println("- MessageRead\t\t: .findById(message3.getId())");
        System.out.println("\t"+ messageService.find(message3.getId()));
        System.out.println();
        // DELETE
        System.out.println("- MessageDelete\t\t: .deleteById(message2.getId())");
        messageService.delete(message2.getId());
        System.out.println("- MessageRead\t\t: .findById(message2.getId())");
        try {
            System.out.println("\t"+ messageService.find(message2.getId()));
        } catch (NoSuchElementException e) {
//            e.printStackTrace();
            System.out.println("\t\t해당 메시지를 찾을 수 없습니다");

        }
        System.out.println("----------------메시지 테스트 끝!!!---------------------\n\n\n");
    }
}
