package com.sprint.mission.discodeit;

import java.util.ArrayList;
import java.util.Optional;
import com.sprint.mission.discodeit.data.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.data.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.data.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    static User setupUser(UserService userService) {
        UserCreateRequest request = new UserCreateRequest("장현우", "1234", "woody@codeit.com", Optional.empty());
        User user = userService.create(request);
        return user;
    }

    static Channel setupChannel(User user, ChannelService channelService) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(ChannelType.TEXT, "Public", "Public Channel");
        Channel channel = channelService.create(request);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        MessageCreateRequest request = new MessageCreateRequest(channel.getId(), author.getId(), "내용", Optional.empty());
        Message message = messageService.create(request);
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
        // 서비스 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(user, channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}
