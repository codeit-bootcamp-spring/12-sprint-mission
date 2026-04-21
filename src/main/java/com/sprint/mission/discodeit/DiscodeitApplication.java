package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        runSmokeTest(context);
    }

    private static void runSmokeTest(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();

        String repoType = env.getProperty("discodeit.repository.type", "jcf");
        String fileDirectory = env.getProperty("discodeit.repository.file-directory", ".discodeit");

        UserRepository userRepository = context.getBean(UserRepository.class);
        ChannelRepository channelRepository = context.getBean(ChannelRepository.class);
        MessageRepository messageRepository = context.getBean(MessageRepository.class);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);

        long suffix = System.currentTimeMillis();
        String username = "woody" + suffix;
        String email = "woody" + suffix + "@codeit.com";
        String password = "woody1234";

        System.out.println("========== SMOKE TEST START ==========");
        System.out.println("[CONFIG] repository.type = " + repoType);
        System.out.println("[CONFIG] repository.file-directory = " + fileDirectory);

        System.out.println("[BEAN] UserRepository = " + userRepository.getClass().getSimpleName());
        System.out.println("[BEAN] ChannelRepository = " + channelRepository.getClass().getSimpleName());
        System.out.println("[BEAN] MessageRepository = " + messageRepository.getClass().getSimpleName());

        UserResponse user = userService.create(
                new UserCreateRequest(
                        username,
                        email,
                        password,
                        null
                )
        );
        System.out.println("[CREATE] userId = " + user.id());

        ChannelResponse channel = channelService.createPublic(
                new PublicChannelCreateRequest(
                        "공지-" + suffix,
                        "스모크 테스트용 공개 채널"
                )
        );
        System.out.println("[CREATE] channelId = " + channel.id());

        MessageResponse message = messageService.create(
                new MessageCreateRequest(
                        "안녕하세요. 스모크 테스트 메시지입니다.",
                        channel.id(),
                        user.id(),
                        List.of()
                )
        );
        System.out.println("[CREATE] messageId = " + message.id());

        AuthResponse loginResult = authService.login(
                new LoginRequest(username, password)
        );
        System.out.println("[LOGIN] success userId = " + loginResult.id());

        List<ChannelResponse> channels = channelService.findAllByUserId(user.id());
        List<MessageResponse> messages = messageService.findAllByChannelId(channel.id());

        System.out.println("[QUERY] visible channel count = " + channels.size());
        System.out.println("[QUERY] message count in channel = " + messages.size());

        System.out.println("=========== SMOKE TEST END ===========");
    }
}