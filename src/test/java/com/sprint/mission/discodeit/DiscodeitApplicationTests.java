package com.sprint.mission.discodeit;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DiscodeitApplicationTests {

	@Autowired
	private UserController userController;

	@Autowired
	private ChannelController channelController;

	@Autowired
	private MessageController messageController;

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("유저 생성 및 조회 통합 테스트 - 성공")
	void userCreateAndFindAll_Success() {
		UserCreateRequest request = new UserCreateRequest("통합유저", "integration@example.com", "pass123");

		ResponseEntity<UserDto> createResponse = userController.create(request, null);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();

		ResponseEntity<List<UserDto>> listResponse = userController.findAll();
		assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(listResponse.getBody()).extracting("username").contains("통합유저");
	}

	@Test
	@DisplayName("존재하지 않는 유저 삭제 통합 테스트 - 실패")
	void userDelete_Fail_NotFound() {
		UUID invalidUserId = UUID.randomUUID();

		assertThatThrownBy(() -> userController.delete(invalidUserId))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	@DisplayName("공개 채널 생성 통합 테스트 - 성공")
	void channelCreate_Success() {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("통합채널", "테스트설명");

		ResponseEntity<ChannelDto> response = channelController.create(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("통합채널");
	}

	@Test
	@DisplayName("존재하지 않는 채널 삭제 통합 테스트 - 실패")
	void channelDelete_Fail_NotFound() {
		UUID invalidChannelId = UUID.randomUUID();

		assertThatThrownBy(() -> channelController.delete(invalidChannelId))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	@DisplayName("메시지 생성 및 목록 조회 통합 테스트 - 성공")
	void messageCreateAndFindAll_Success() {
		UserCreateRequest userRequest = new UserCreateRequest("메시지유저", "msg@example.com", "pass");
		UserDto user = userController.create(userRequest, null).getBody();

		PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest("메시지채널", "설명");
		ChannelDto channel = channelController.create(channelRequest).getBody();

		MessageCreateRequest messageRequest = new MessageCreateRequest("통합 테스트 메시지", channel.id(), user.id());
		ResponseEntity<MessageDto> createResponse = messageController.create(messageRequest, null);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		ResponseEntity<PageResponse<MessageDto>> listResponse =
			messageController.findAllByChannelId(channel.id(), Instant.now(), null);

		assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(listResponse.getBody().content()).isNotEmpty();
	}

	@Test
	@DisplayName("존재하지 않는 메시지 삭제 통합 테스트 - 실패")
	void messageDelete_Fail_NotFound() {
		UUID invalidMessageId = UUID.randomUUID();

		assertThatThrownBy(() -> messageController.delete(invalidMessageId))
			.isInstanceOf(RuntimeException.class);
	}

}
