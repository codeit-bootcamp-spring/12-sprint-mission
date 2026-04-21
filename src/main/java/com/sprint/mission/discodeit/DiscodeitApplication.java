package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.RepositoryProperties;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateRequest;

@SpringBootApplication
@EnableConfigurationProperties(RepositoryProperties.class)
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);

        UserResponse tempUser = setupUser(userService);
        Channel tempChannel = setupChannel(channelService);
        messageCreateTest(messageService, tempChannel, tempUser.getId(), tempUser.getUsername());

        System.out.println("\n========================\n");

        UserResponse user1 = userService.create(
                new UserCreateRequest("user1", "user1@email.com", "1234", "닉네임1",
                        null, null, null)
        );

        UserResponse user2 = userService.create(
                new UserCreateRequest("user2", "user2@email.com", "5678", "닉네임2",
                        null, null, null)
        );

        System.out.println("=== 등록 완료 ===");
        System.out.println(user1);
        System.out.println(user2);

        UserResponse foundUser = userService.findById(user1.getId());
        System.out.println("=== 단건 조회 ===");
        System.out.println(foundUser);

        System.out.println("=== 전체 조회 ===");
        for (UserResponse u : userService.findAll()) {
            System.out.println(u);
        }

        UserResponse updatedUser = userService.update(
                user1.getId(),
                new UserUpdateRequest(
                        user1.getId(),
                        "user111",
                        "user111@email.com",
                        "9999",
                        "user111",
                        null, null, null
                )
        );

        System.out.println("=== 수정 후 조회 ===");
        System.out.println(updatedUser);

        System.out.println("=== 로그인 테스트 ===");
        UserResponse loginUser = authService.login(
                new LoginRequest("user2", "5678")
        );
        System.out.println(loginUser);

        userService.delete(user1.getId());

        System.out.println("=== 삭제 후 전체 조회 ===");
        for (UserResponse u : userService.findAll()) {
            System.out.println(u);
        }

        Channel channel = channelService.createPublic(
                new ChannelCreatePublicRequest("일반 채널", "테스트 채널")
        );

        Channel privateChannel = channelService.createPrivate(
                new ChannelCreatePrivateRequest(
                        java.util.List.of(user2.getId(), tempUser.getId())
                )
        );

        System.out.println("=== 비공개 채널 생성 ===");
        System.out.println(privateChannel);

        System.out.println("=== 채널 생성 ===");
        System.out.println(channel);

        System.out.println("=== user2가 볼 수 있는 채널 목록 ===");
        for (Channel c : channelService.findAllByUserId(user2.getId())) {
            System.out.println(c);
        }

        System.out.println("=== woody가 볼 수 있는 채널 목록 ===");
        for (Channel c : channelService.findAllByUserId(tempUser.getId())) {
            System.out.println(c);
        }

        System.out.println("=== 비공개 채널 수정 테스트 ===");
        try {
            Channel updateChannel = new Channel("수정된 채널", "설명 수정");
            channelService.update(privateChannel.getId(), updateChannel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("=== 비공개 채널 삭제 테스트 ===");
        channelService.delete(privateChannel.getId());
        System.out.println("비공개 채널 삭제 완료");

        System.out.println("=== 삭제 후 user2가 볼 수 있는 채널 목록 ===");
        for (Channel c : channelService.findAllByUserId(user2.getId())) {
            System.out.println(c);
        }

        System.out.println("=== 사용자 서비스 DTO 전환 완료 ===");
    }

    static UserResponse setupUser(UserService userService) {
        return userService.create(
                new UserCreateRequest("woody", "woody@codeit.com", "1234", "우디",
                        null, null, null)
        );
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.createPublic(
                new ChannelCreatePublicRequest("공지", "공지 채널입니다.")
        );
    }


    static void messageCreateTest(MessageService messageService, Channel channel, java.util.UUID authorId, String authorName) {

        System.out.println("=== 메시지 생성 ===");

        Message message = messageService.create(
                new MessageCreateRequest(
                        "안녕하세요",
                        authorId,
                        channel.getId(),
                        null
                )
        );

        System.out.println(message);

        System.out.println("=== 채널별 메시지 조회 ===");

        for (Message m : messageService.findAllByChannelId(channel.getId())) {
            System.out.println(m);
        }

        System.out.println("=== 메시지 수정 ===");

        Message updated = messageService.update(
                message.getId(),
                new MessageUpdateRequest(
                        message.getId(),
                        "수정된 메시지입니다"
                )
        );

        System.out.println(updated);

        System.out.println("=== 메시지 삭제 ===");

        messageService.delete(message.getId());

        System.out.println("삭제 완료");

        System.out.println("=== 삭제 후 메시지 조회 ===");

        for (Message m : messageService.findAllByChannelId(channel.getId())) {
            System.out.println(m);
        }
    }
}