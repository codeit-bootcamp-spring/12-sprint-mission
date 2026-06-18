package com.sprint.mission.discodeit.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@EnableJpaAuditing
@DataJpaTest
public class MessageRepositoryTest {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private UserRepository userRepository;


	@Test
	@DisplayName("findAllByChannelIdWithAuthor - 성공: 특정 채널의 메시지 목록을 커서(createdAt) 기반 Slice 페이징으로 조회한다")
	void findAllByChannelIdWithAuthor_Success() {
		// given
		Channel channel = new Channel(ChannelType.PUBLIC, "개발 채널", "개발 관련 대화");
		channelRepository.save(channel);

		BinaryContent profile = new BinaryContent("avatar.png", 500L, "image/png");
		User author = new User("coder", "coder@example.com", "pass", profile);
		UserStatus status = new UserStatus(author, Instant.now());

		userRepository.save(author);
		Message msg1 = new Message("첫 번째 메시지", channel, author, List.of());
		Message msg2 = new Message("두 번째 메시지", channel, author, List.of());

		messageRepository.saveAll(List.of(msg1, msg2));
		Instant cursorTime = Instant.now().plusSeconds(10);
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		// when
		Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channel.getId(), cursorTime, pageable);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getAuthor().getUsername()).isEqualTo("coder");
		assertThat(result.getContent().get(0).getAuthor().getStatus()).isNotNull(); // 패치 조인 확인
	}

	@Test
	@DisplayName("findAllByChannelIdWithAuthor - 실패/데이터없음: 검색 기준 시간(createdAt) 조건보다 과거에 작성된 메시지가 없으면 빈 Slice를 반환한다")
	void findAllByChannelIdWithAuthor_Fail_NoMessagesBeforeCursor() {
		// given
		Channel channel = new Channel(ChannelType.PUBLIC, "자유 채널", "자유 대화");
		channelRepository.save(channel);

		User author = new User("user", "user@example.com", "pass", null);
		new UserStatus(author, Instant.now());
		userRepository.save(author);

		Message msg = new Message("방금 쓴 메시지", channel, author, List.of());
		messageRepository.save(msg);

		Instant pastCursorTime = Instant.now().minusSeconds(100);
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channel.getId(), pastCursorTime, pageable);

		// then
		assertThat(result.getContent()).isEmpty();
	}


	@Test
	@DisplayName("findLastMessageAtByChannelId - 성공: 해당 채널에 작성된 마지막 메시지의 생성 시간을 정확히 조회한다")
	void findLastMessageAtByChannelId_Success() {
		// given
		Channel channel = new Channel(ChannelType.PUBLIC, "테스트 채널", "설명");
		channelRepository.save(channel);

		User author = new User("writer", "writer@example.com", "pass", null);
		new UserStatus(author, Instant.now());
		userRepository.save(author);

		Message oldMsg = new Message("예전 메시지", channel, author, List.of());
		messageRepository.save(oldMsg);

		Message newMsg = new Message("가장 최근 메시지", channel, author, List.of());
		messageRepository.save(newMsg);

		// when
		Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(newMsg.getCreatedAt());
	}

	@Test
	@DisplayName("findLastMessageAtByChannelId - 실패/데이터없음: 메시지가 전혀 없는 채널의 ID로 조회하면 Optional.empty()를 반환한다")
	void findLastMessageAtByChannelId_Fail_EmptyChannel() {
		// given
		Channel emptyChannel = new Channel(ChannelType.PUBLIC, "빈 채널", "대화 없음");
		channelRepository.save(emptyChannel);

		// when
		Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(emptyChannel.getId());

		// then
		assertThat(result).isEmpty();
	}


	@Test
	@DisplayName("deleteAllByChannelId - 성공: 채널 ID를 기준으로 해당 채널의 모든 메시지를 일괄 삭제한다")
	void deleteAllByChannelId_Success() {
		// given
		Channel channel = new Channel(ChannelType.PUBLIC, "삭제될 채널", "설명");
		channelRepository.save(channel);

		User author = new User("member", "member@example.com", "pass", null);
		new UserStatus(author, Instant.now());
		userRepository.save(author);

		Message msg1 = new Message("삭제될 메시지 1", channel, author, List.of());
		Message msg2 = new Message("삭제될 메시지 2", channel, author, List.of());
		messageRepository.saveAll(List.of(msg1, msg2));

		// when
		messageRepository.deleteAllByChannelId(channel.getId());

		// then
		List<Message> remainingMessages = messageRepository.findAll();
		assertThat(remainingMessages).isEmpty();
	}

	@Test
	@DisplayName("deleteAllByChannelId - 실패/영향없음: 존재하지 않는 채널 ID로 삭제 요청 시 예외 없이 안정적으로 종료된다")
	void deleteAllByChannelId_Fail_NonExistentChannelId() {
		// given
		UUID nonExistentChannelId = UUID.randomUUID();

		assertThatCode(() -> messageRepository.deleteAllByChannelId(nonExistentChannelId))
			.doesNotThrowAnyException();
	}

}
