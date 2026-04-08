package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.ChannelCreatePublicRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {
	static User setupUser(UserService userService) {
		User user = userService.create("woody", "woody@codeit.com", "woody1234", null);
		System.out.println("유저 생성: " + user.toString());
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create(new ChannelCreatePublicRequestDto("공지", "공지 채널입니다."));
		System.out.println("채널 생성: " + channel);
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create(new MessageCreateRequestDto("안녕하세요.", channel.getId(), author.getId()));
		System.out.println("메시지 생성: " + message);
	}

	public static void main(String[] args) {
		// 레포지토리 초기화
		UserRepository userRepository = new FileUserRepository(".discodeit");
		ChannelRepository channelRepository = new FileChannelRepository(".discodeit");
		MessageRepository messageRepository = new FileMessageRepository(".discodeit");
		BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(".discodeit");
		ReadStatusRepository readStatusRepository = new FileReadStatusRepository(".discodeit");
		UserStatusRepository userStatusRepository = new FileUserStatusRepository(".discodeit");

		// 서비스 초기화
		UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
		ChannelService channelService = new BasicChannelService(channelRepository, readStatusRepository,
			messageRepository);
		MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository,
			binaryContentRepository);

		// 셋업
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		// 테스트
		messageCreateTest(messageService, channel, user);
	}
}
