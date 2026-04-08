package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	static User setupUser(UserService userService) {
		return userService.create("woody", "woody@codeit.com", "woody1234");
	}

	static Channel setupChannel(ChannelService channelService) {
		return channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getId());

		Message foundMessage = messageService.find(message.getId());
		System.out.println("메시지 조회(단건): " + foundMessage.getId());

		System.out.println("메시지 조회(다건): " + messageService.findAll().size());

		Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
		System.out.println("메시지 수정: " + updatedMessage.getContent());

		messageService.delete(message.getId());
		System.out.println("메시지 삭제 후 다건 조회: " + messageService.findAll().size());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		messageCreateTest(messageService, channel, user);
	}
}