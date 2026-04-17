package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;



@SpringBootApplication
public class DiscodeitApplication {

	static void userCRUDTest (UserService userService) {
		// 생성
		UserDto user = userService.create(
				new CreateUserRequest(
						"Woody",
						"woody@codeit.com",
						"woody1234",
						null
				)
		);
		System.out.println("유저 생성: " + user.id());

		// 조회
		UserDto foundUser = userService.find(user.id());
		System.out.println("유저 조회(단건): " + foundUser.id());

		List<UserDto> foundUsers = userService.findAll();
		System.out.println("유저 조회(다건): " + foundUsers.size());

		// 수정
		UserDto updatedUser = userService.update(
				user.id(),
				new UpdateUserRequest(
						"newWoody",
						"newWoody@codeit.com",
						"newWoody1234",
						null
				));
		System.out.println("유저 수정: " + String.join("/", updatedUser.username(), updatedUser.email(), String.valueOf(updatedUser.online())));

		// 삭제
		userService.delete(user.id());
		List<UserDto> foundUsersAfterDelete = userService.findAll();
		System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
	}

	static void publicChannelCRUDTest(ChannelService channelService, UserService userService) {

		// 조회용 유저 생성
		UserDto viewer = userService.create(
				new CreateUserRequest("viewer", "viewer@codeit.com", "viewer1234", null)
		);

		// 생성
		Channel channel = channelService.createPublic(
				new CreatePublicChannelRequest("공지", "공지 채널입니다.")
		);
		System.out.println("PUBLIC 채널 생성: " + channel.getId());

		// 조회(단건)
		ChannelDto foundChannel = channelService.find(channel.getId());
		System.out.println("채널 조회(단건): " + foundChannel.id());

		// 조회(다건)
		List<ChannelDto> foundChannels = channelService.findAllByUserId(viewer.id());
		System.out.println("채널 조회(다건): " + foundChannels.size());

		// 수정
		Channel updatedChannel = channelService.update(
				new UpdateChannelRequest(channel.getId(), "공지사항", "수정된 공지")
		);
		System.out.println("채널 수정: " + updatedChannel.getName());

		// 삭제
		channelService.delete(channel.getId());

		List<ChannelDto> afterDelete = channelService.findAllByUserId(viewer.id());
		boolean existsAfterDelete = afterDelete.stream()
				.anyMatch(c -> c.id().equals(channel.getId()));
		System.out.println("삭제 후 존재 여부: " + existsAfterDelete);
	}

	static void privateChannelCRUDTest(ChannelService channelService, UserService userService) {

		// 유저 생성 (DTO 기반)
		UserDto user1 = userService.create(
				new CreateUserRequest("buzz", "buzz@codeit.com", "buzz1234", null)
		);
		UserDto user2 = userService.create(
				new CreateUserRequest("jessie", "jessie@codeit.com", "jessie1234", null)
		);
		UserDto outsider = userService.create(
				new CreateUserRequest("rex", "rex@codeit.com", "rex1234", null)
		);

		// 생성
		Channel privateChannel = channelService.createPrivate(
				new CreatePrivateChannelRequest(List.of(user1.id(), user2.id()))
		);
		System.out.println("PRIVATE 채널 생성: " + privateChannel.getId());

		// 조회(단건)
		ChannelDto foundPrivateChannel = channelService.find(privateChannel.getId());
		System.out.println("PRIVATE 채널 조회(단건): " + foundPrivateChannel.id());
		System.out.println("PRIVATE 참여자 목록: " + foundPrivateChannel.userIds());

		// 조회(다건)
		List<ChannelDto> user1Channels = channelService.findAllByUserId(user1.id());
		System.out.println("user1 채널 수: " + user1Channels.size());

		List<ChannelDto> user2Channels = channelService.findAllByUserId(user2.id());
		System.out.println("user2 채널 수: " + user2Channels.size());

		List<ChannelDto> outsiderChannels = channelService.findAllByUserId(outsider.id());
		boolean outsiderCanSee = outsiderChannels.stream()
				.anyMatch(c -> c.id().equals(privateChannel.getId()));
		System.out.println("비참여자 접근 가능 여부: " + outsiderCanSee);

		// 수정 불가
		try {
			channelService.update(
					new UpdateChannelRequest(privateChannel.getId(), "수정", "수정")
			);
			System.out.println("수정됨 X");
		} catch (Exception e) {
			System.out.println("수정 불가 ✔️");
		}

		// 삭제
		channelService.delete(privateChannel.getId());
	}

	static void messageCRUDTest(MessageService messageService,
	                            ChannelService channelService,
	                            UserService userService) {

		// ✅ 여기서 생성 (이게 정답)
		UserDto user = userService.create(
				new CreateUserRequest(
						"messageUser",
						"messageUser@test.com",
						"password1234",
						null
				)
		);

		Channel channel = channelService.createPublic(
				new CreatePublicChannelRequest(
						"메시지 채널",
						"메시지 테스트 채널"
				)
		);

		UUID channelId = channel.getId();
		UUID authorId = user.id();

		// =====================
		// 생성
		// =====================
		CreateMessageRequest createRequest = new CreateMessageRequest(
				"안녕하세요.",
				channelId,
				authorId,
				List.of()
		);

		Message message = messageService.create(createRequest);
		System.out.println("메시지 생성: " + message.getId());

		// 조회
		Message foundMessage = messageService.find(message.getId());
		System.out.println("메시지 조회(단건): " + foundMessage.getId());

		List<Message> foundMessages = messageService.findAllByChannelId(channelId);
		System.out.println("메시지 조회(다건): " + foundMessages.size());

		// 수정
		UpdateMessageRequest updateRequest = new UpdateMessageRequest(
				message.getId(),
				"반갑습니다."
		);

		Message updatedMessage = messageService.update(updateRequest);
		System.out.println("메시지 수정: " + updatedMessage.getContent());

		// 삭제
		messageService.delete(message.getId());

		List<Message> afterDelete = messageService.findAllByChannelId(channelId);
		System.out.println("메시지 삭제: " + afterDelete.size());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// 서비스 초기화
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);


		// 테스트
		userCRUDTest(userService);
		publicChannelCRUDTest(channelService, userService);
		privateChannelCRUDTest(channelService, userService);
		messageCRUDTest(messageService, channelService, userService);
	}

}
