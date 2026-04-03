package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelSevice;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelSevice;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

import java.io.File;

public class JavaApplication {
    static User setupUser(BasicUserService userService) {
        return userService.create("woody", "woody@codeit.com", "woody1234");
    }

    static Channel setupChannel(BasicChannelSevice channelService) {
        return channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
    }

    static void messageCreateTest(BasicMessageService messageService, Channel channel, User author) {
        messageService.create(author.getId(), channel.getId(),"안녕하세요." );
    }
    public static void main(String[] args) {

        new File("users.data").delete();
        new File("channels.data").delete();
        new File("messages.data").delete();

        FileUserRepository userRepository = new FileUserRepository();
        FileChannelRepository channelRepository = new FileChannelRepository();
        FileMessageRepository messageRepository = new FileMessageRepository();

        BasicUserService userService = new BasicUserService(userRepository);
        BasicChannelSevice channelService = new BasicChannelSevice(channelRepository);
        BasicMessageService messageService = new BasicMessageService(messageRepository);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);

        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());


    }
}