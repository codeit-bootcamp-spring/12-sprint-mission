package com.sprint.mission.discodeit;

// 🔥 우리가 만든 DTO 클래스들 import 필요! (경로는 운정이 프로젝트에 맞게 수정해줘)
import com.sprint.mission.discodeit.dto.data.channel.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.dto.data.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.data.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.data.message.MessageResponse;
import com.sprint.mission.discodeit.dto.data.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.data.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.user.UserResponse;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	// 💡 1. 반환 타입이 User -> UserResponse로 변경됨!
	static UserResponse setupUser(UserService userService) {
		// 프로필 이미지 파일은 테스트니까 null로 넘김!
		UserCreateRequest request = new UserCreateRequest("woody", "woody@codeit.com", "woody1234", null, null, null);
		return userService.create(request);
	}

	// 💡 2. 반환 타입이 Channel -> ChannelResponse로 변경됨!
	static ChannelResponse setupChannel(ChannelService channelService) {
		// 퍼블릭 채널 생성용 DTO 사용! (채널 서비스 메서드명은 네가 지은 거에 맞게 바꿔!)
		ChannelCreatePublicRequest request = new ChannelCreatePublicRequest("공지", "공지 채널입니다.");
		return channelService.createPublic(request);
	}

	// 💡 3. 파라미터 타입들도 전부 DTO(Response)로 변경!
	static void messageCreateTest(MessageService messageService, ChannelResponse channel, UserResponse author) {
		// DTO는 record니까 getter 대신 id() 형태로 접근! 첨부파일 리스트는 null.
		MessageCreateRequest createRequest = new MessageCreateRequest("안녕하세요.", channel.id(), author.id(), null);
		MessageResponse message = messageService.create(createRequest);

		System.out.println("메시지 생성: " + message.id());

		MessageResponse foundMessage = messageService.find(message.id());
		System.out.println("메시지 조회(단건): " + foundMessage.id());

		// 💡 4. findAll() 대신 우리가 리팩토링한 findAllByChannelId() 사용!
		System.out.println("메시지 조회(다건): " + messageService.findAllByChannelId(channel.id()).size());

		MessageUpdateRequest updateRequest = new MessageUpdateRequest("반갑습니다.");
		MessageResponse updatedMessage = messageService.update(message.id(), updateRequest);
		System.out.println("메시지 수정: " + updatedMessage.content());

		messageService.delete(message.id());
		System.out.println("메시지 삭제 후 다건 조회: " + messageService.findAllByChannelId(channel.id()).size());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean("basicUserService", UserService.class);
		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);

		// 테스트 실행!
		UserResponse user = setupUser(userService);
		ChannelResponse channel = setupChannel(channelService);
		messageCreateTest(messageService, channel, user);
	}
}