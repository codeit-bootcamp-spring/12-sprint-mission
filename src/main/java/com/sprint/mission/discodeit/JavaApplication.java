package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;


public class JavaApplication {
    public static void main(String[] args) {
        // 사용자 테스트 시작!!
        System.out.println("----------------사용자 테스트 시작!!!------------------");

        UserRepository userRepository = new JCFUserRepository();
        UserService userService = new JCFUserService(userRepository);
        userService.createUser("test", "test@email.com", "1234", "test");
        System.out.println(userService.getAllUsers());

        System.out.println("---------------사용자 테스트 끝!!!---------------------\n");
    }
}
