package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    static List<UserResponse> setupUser(UserService userService) {
        List<UserResponse> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            users.add(userService.create(
                    new UserCreateRequest(
                            "woody" + i,
                            "woody" + i + "@gmail.com",
                            "Nick" + i,
                            "1234",
                            null,
                            null)
            ));
            System.out.println(
                    "유저 생성: " + users.get(i - 1).id()
                            + "\n닉네임: " + users.get(i - 1).nickname()
            );
            System.out.println();
        }

        return users;
    }

    static ChannelResponse setupPublicChannel(ChannelService channelService, UUID userId, int index) {
        ChannelResponse channel = channelService.createPublicChannel(
                new ChannelCreateRequest(
                        "공지" + index,
                        userId,
                        "공지 채널입니다.")
        );
        System.out.println(
                "채널 이름: " + channel.title()
                        + "\n채널 생성자: " + channel.userId()
        );

        return channel;
    }

    static void setupMessage(MessageService messageService, UUID channelId, UUID userId, int index) {
        MessageResponse message = messageService.create(
                new MessageCreateRequest(
                        channelId,
                        userId,
                        "메시지 작성" + index,
                        "안녕하세요." + index,
                        new ArrayList<>()
                )
        );
        System.out.println(
                "메시지 생성: " + message.id()
                        + "\n제목: " + message.title()
                        + "\n내용: " + message.content()
        );
    }

    static void displayAllData(UserService userService,
                               ChannelService channelService,
                               MessageService messageService,
                               UserStatusService userStatusService,
                               ReadStatusService readStatusService) {
        int random = new Random().nextInt(0, 5);
        System.out.println("전체 유저: " + userService.findAll().stream().map(UserResponse::nickname).toList());
        List<ChannelResponse> channelList = channelService.findAll();
        System.out.println("전체 채널: " + channelList.stream().map(ChannelResponse::title).toList());
        System.out.println("채널 " + random + 1 + " 소속 메시지: " + messageService.findByChannelId(channelList.get(random).id()).stream().map(MessageResponse::title).toList());
        System.out.println("User Status :" + userStatusService.findAll().stream()
                .map(status -> userService.findById(status.userId()).isOnline())
                .toList());
        System.out.println("Read Status :" + readStatusService.finAllByUserId(
                        userService.findAll().get(random).id()).stream()
                .map(status -> userService.findById(status.userId()))
                .toList()
        );
    }

    static void login(AuthService authService) {
        int random = new Random().nextInt(1, 6);
        LoginResponse auth = authService.login(new LoginRequest("woody" + random, "1234"));
        System.out.println("로그인 성공 " + auth);

    }

    static void updateObject(UserService userService, ChannelService channelService, MessageService messageService) {
        int random = new Random().nextInt(0, 5);
        UserResponse user = userService.findAll().get(random);
        UserUpdateRequest updateUser = new UserUpdateRequest(user.id(), "비밀번호가 안보여서 이름 변경", null, null);
        userService.update(updateUser);
        System.out.println("유저 "
                + user.nickname()
                + " 정보 업데이트 결과: "
                + userService.findById(user.id())
        );

        ChannelResponse channel = channelService.findAllByUserId(user.id()).get(random);
        ChannelUpdateRequest updateChannel = new ChannelUpdateRequest(
                channel.id(), null, "채널 정보 업데이트");
        channelService.update(updateChannel);
        System.out.println("채널 "
                + channel.title()
                + " 정보 업데이트 결과: "
                + channelService.findById(channel.id())
        );

        MessageResponse message = messageService.findByChannelId(channel.id()).get(0);
        MessageUpdateRequest updateMessage = new MessageUpdateRequest(
                message.id(), null, "메시지 내용 업데이트");
        messageService.update(updateMessage);
        System.out.println("채널 "
                + message.title()
                + " 정보 업데이트 결과: "
                + messageService.findByChannelId(channel.id())
        );
    }

    static void deleteData(UserService userService,
                           ChannelService channelService
    ) {
        int random = new Random().nextInt(0, 5);
        UserResponse user = userService.findAll().get(random);
        ChannelResponse channelId = channelService.findAllByUserId(user.id()).get(random);

        System.out.println("유저 " + userService.findAll().get(random).nickname() + " 삭제");
        userService.delete(user.id());
        System.out.println(userService.findAll().stream().map(UserResponse::nickname).toList());
        System.out.println("채널 " + channelService.findAllByUserId(user.id()).get(random).title() + " 삭제");
        channelService.delete(channelId.id());
        System.out.println(channelService.findAll().stream().map(ChannelResponse::title).toList());

    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
        System.out.println("http://localhost:8080/");

        System.out.println(Instant.now().toString());
        System.out.println();

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);

        System.out.println("======= [1] entity 생성 =======");
        List<UserResponse> users = setupUser(userService);
        int index = 1;
        for (UserResponse user : users) {
            ChannelResponse channel = setupPublicChannel(channelService, user.id(), index);
            setupMessage(messageService, channel.id(), user.id(), index);
            index++;
            System.out.println();
        }

        System.out.println("======= [2] 서비스 작동 확인 =======");
        displayAllData(userService, channelService, messageService, userStatusService, readStatusService);
        login(authService);
        System.out.println();

        System.out.println("======= [3] 서비스 내용 수정 =======");
        updateObject(userService, channelService, messageService);
        System.out.println();

        System.out.println("======= [4] 데이터 삭제 =======");
        deleteData(userService, channelService);
        System.out.println();

        System.out.println("======= Test 종료 =======");


    }
}

