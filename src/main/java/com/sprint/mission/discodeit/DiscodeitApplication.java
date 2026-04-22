package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class DiscodeitApplication {

    // 레포지토리 초기화
        UserRepository userRepository = new FileUserRepository();
    ChannelRepository channelRepository = new FileChannelRepository();
    MessageRepository messageRepository = new FileMessageRepository();

    public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// Service 초기화
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// TEST CODE
		UserDto user = setupUser(userService);
		ChannelDto publicChannel = setupPublicChannel(channelService);
		ChannelDto privateChannel = setupPrivateChannel(channelService, user);
		messageCreateTest(messageService, publicChannel, user);
		userUpdateTest(userService, user);
		userDeleteTest(userService);
	}

	// 유저 생성 (프로필 이미지 없이)
	static UserDto setupUser(UserService userService) {
		UserCreateRequest request = new UserCreateRequest("woody", "woody@codeit.com", "woody1234", null);
		UserDto user = userService.create(request);
		System.out.println("[유저 생성] id=" + user.getId() + " username=" + user.getUsername() + " online=" + user.isOnline());
		return user;
	}

	// PUBLIC 채널 생성
	static ChannelDto setupPublicChannel(ChannelService channelService) {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", "공지 채널");
		ChannelDto channel = channelService.createPublic(request);
		System.out.println("[PUBLIC 채널 생성] id=" + channel.getId() + " name=" + channel.getName());
		return channel;
	}

	// PRIVATE 채널 생성
	static ChannelDto setupPrivateChannel(ChannelService channelService, UserDto user) {
		PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
				java.util.List.of(user.getId())
		);
		ChannelDto channel = channelService.createPrivate(request);
		System.out.println("[PRIVATE 채널 생성] id=" + channel.getId() + " participants=" + channel.getParticipantIds());
		return channel;
	}

	// 메시지 생성 (첨부파일 없이)
	static void messageCreateTest(MessageService messageService, ChannelDto channel, UserDto user) {
		MessageCreateRequest request = new MessageCreateRequest(
				"안녕하세요.",
				channel.getId(),
				user.getId(),
				null
		);
		Message message = messageService.create(request);
		System.out.println("[메시지 생성] id=" + message.getId() + " content=" + message.getContent());
	}

	// 유저 수정 (프로필 이미지 없이)
	static void userUpdateTest(UserService userService, UserDto user) {
		UserUpdateRequest request = new UserUpdateRequest("woody_updated", null, null, null);
		UserDto updated = userService.update(user.getId(), request);
		System.out.println("[유저 수정] username=" + updated.getUsername());
	}

	// 중복 username 예외 테스트
	static void userDeleteTest(UserService userService) {
		try {
			UserCreateRequest duplicate = new UserCreateRequest("woody", "other@codeit.com", "pass1234", null);
			userService.create(duplicate);
		} catch (IllegalArgumentException e) {
			System.out.println("[중복 예외 확인] " + e.getMessage());
		}
	}
}









