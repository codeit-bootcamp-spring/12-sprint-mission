package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelCreateDTO;
import com.sprint.mission.discodeit.dto.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.UserCreateDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.beans.factory.annotation.Value;

public class JavaApplication {

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
        // 레포지토리 초기화
        String filePath = "./file-data-map";
        UserRepository userRepository = new FileUserRepository(filePath);
        ChannelRepository channelRepository = new FileChannelRepository(filePath);
        MessageRepository messageRepository = new FileMessageRepository(filePath);
        UserStatusRepository userStatusRepository = new FileUserStatusRepository(filePath);
        ReadStatusRepository readStatusRepository = new FileReadStatusRepository(filePath);
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(filePath);

        // 서비스 초기화
        UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, messageRepository, readStatusRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentRepository);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}
