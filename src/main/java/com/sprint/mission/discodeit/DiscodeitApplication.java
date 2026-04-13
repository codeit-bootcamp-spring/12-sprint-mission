package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);

		userTest(userService, user);
		channelTest(channelService, channel);
		messageTest(messageService, user, channel);
	}

	static User setupUser(UserService userService) {
		User user = new User("setupUser", "setupUser@email.com", "12345678", "setupUser", null);
		userService.save(user);
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = new Channel(ChannelType.PUBLIC, "공지방");
		channelService.save(channel);
		return channel;
	}

	static void userTest(UserService userService, User user) {
		System.out.println("\n-----------------사용자 테스트 시작-----------------");

		System.out.println("===== 사용자 전체 조회 =====");
		System.out.println(userService.findAll());

		System.out.println("===== 사용자 단일 조회 =====");
		System.out.println(userService.findById(user.getId()));

		System.out.println("===== 사용자 정보 수정 =====");
		userService.update(user.getId(), "update", "update@example.com", "1111", "hihi");
		System.out.println(userService.findById(user.getId()));

		System.out.println("-----------------사용자 테스트 종료-----------------\n");
	}

	static void channelTest(ChannelService channelService, Channel channel) {
		System.out.println("\n-----------------채널 테스트 시작-----------------");

		System.out.println("===== 채널 전체 조회 =====");
		System.out.println(channelService.findAll());

		System.out.println("===== 채널 수정 =====");
		channelService.update(channel.getId(), "★공지방★");
		System.out.println(channelService.findById(channel.getId()));

		System.out.println("-----------------채널 테스트 종료-----------------\n");
	}

	static void messageTest(MessageService messageService, User user, Channel channel) {
		System.out.println("\n-----------------메시지 테스트 시작-----------------");

		Message message = new Message(user.getId(), channel.getId(), "안녕하세요!", null);
		messageService.save(message);

		System.out.println("===== 존재하지 않는 사용자 메시지 저장 테스트 =====");
		try {
			UUID fakeId = UUID.randomUUID();	// 아이디 랜덤 생성
			Message messageNotUser = new Message(fakeId, channel.getId(), "안뇽", null);
			messageService.save(messageNotUser);
			System.out.println("실패: 예외가 발생해야 하는 케이스");
		} catch (NoSuchElementException e) {
			System.out.println("성공: 예외 발생 확인 - " + e.getMessage());
		}

		System.out.println("===== 메시지 수정 및 단건 조회 =====");
		messageService.update(message.getId(), "안녕하세요~!");
		System.out.println(messageService.findById(message.getId()));

		System.out.println("===== 메시지 삭제 =====");
		System.out.println("삭제 전 메시지 수: " + messageService.findAll().size());
		messageService.delete(message.getId());
		System.out.println("삭제 후 메시지 수: " + messageService.findAll().size());

		System.out.println("-----------------메시지 테스트 종료-----------------\n");
	}
}
