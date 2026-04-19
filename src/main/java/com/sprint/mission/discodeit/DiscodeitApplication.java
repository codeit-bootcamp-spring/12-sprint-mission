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
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		System.out.println("http://localhost:8080/");

		ChannelService channelService = context.getBean(ChannelService.class);
		UserService userService = context.getBean(UserService.class);
		MessageService messageService = context.getBean(MessageService.class);

		User user1 = new User("test.com", "1234", "kim", "kk", UUID.randomUUID());
		User user2 = new User("test2.com", "1234", "lee", "ll", UUID.randomUUID());

		Channel textChannel = new Channel("텍스트 채널", ChannelType.TEXT , true);
		Channel voiceChannel = new Channel("음성 채널", ChannelType.VOICE, false);

		Message message1 = new Message(textChannel.getId(), user1.getId(), "메세지 테스트 1", null);
		Message message2 = new Message(textChannel.getId(), user2.getId(), "메세지 테스트 2", null);

		System.out.println("====FILE SERVICE 시작====\n");
		testUser(user1, user2, userService);
		testChannel(textChannel, voiceChannel, channelService);
		testMessage(message1, message2, messageService);
		System.out.println("\n====FILE SERVICE 끝====");


	}


	private static void testUser(User user1, User user2, UserService userService){
		System.out.println("====User save====");
		userService.save(user1);
		userService.save(user2);

		System.out.println("\n====User findById====");
		System.out.println(userService.findById(user1.getId()));
		System.out.println(userService.findById(UUID.randomUUID()));

		System.out.println("\n====User findAll====");
		userService.findAll().forEach(user -> System.out.println(user));

		System.out.println("\n====User update====");
		User changeUser = new User("바뀐 이름", "바뀐 비밀번호", "바뀐 이메일", "바뀐 닉네임", UUID.randomUUID());
		//String userName, String password, String email, String nickName
		System.out.println(userService.update(user1.getId(), changeUser));
		System.out.println("====변경 완료!====");
		userService.findAll().forEach(user -> System.out.println(user));

		System.out.println("====\nUser delete====");
		userService.delete(user2.getId());
		System.out.println("====삭제 완료!====");
		userService.findAll().forEach(user -> System.out.println(user));

	}

	private static void testMessage(Message msg1, Message msg2, MessageService messageService){
		System.out.println("====Message save====");
		messageService.save(msg1);
		messageService.save(msg2);

		System.out.println("\n====Message findById====");
		System.out.println(messageService.findById(msg1.getId()));
		System.out.println(messageService.findById(UUID.randomUUID()));

		System.out.println("\n====Message findAll====");
		messageService.findAll().forEach(msg -> System.out.println(msg));

		System.out.println("\n====Message update====");
		Message changeMessage = new Message(msg1.getId(), msg1.getMemberId(), "바뀐 메세지", null);
		//UUID channelId, UUID memberId, String content
		System.out.println(messageService.update(msg1.getId(), changeMessage, null));
		System.out.println("====변경 완료!====");
		messageService.findAll().forEach(msg -> System.out.println(msg));

		System.out.println("====\nMessage delete====");
		messageService.delete(msg2.getId());
		System.out.println("====삭제 완료!====");
		messageService.findAll().forEach(msg -> System.out.println(msg));

	}

	private static void testChannel(Channel channel1, Channel channel2, ChannelService channelService){
		System.out.println("====Channel save====");
		channelService.save(channel1);
		channelService.save(channel2);

		System.out.println("\n====Channel findById====");
		System.out.println(channelService.findById(channel1.getId()));
		System.out.println(channelService.findById(UUID.randomUUID()));

		System.out.println("\n====Channel findAll====");
		channelService.findAll().forEach(channel -> System.out.println(channel));

		System.out.println("\n====Channel update====");
		Channel changeChannel = new Channel("바뀐 이름", ChannelType.FORUM, false);
		//String name, ChannelType type, boolean isPrivate
		System.out.println(channelService.update(channel1.getId(), changeChannel));
		System.out.println("====변경 완료!====");
		channelService.findAll().forEach(channel -> System.out.println(channel));

		System.out.println("====\nChannel delete====");
		channelService.delete(channel2.getId());
		System.out.println("====삭제 완료!====");
		channelService.findAll().forEach(channel -> System.out.println(channel));
	}

}
