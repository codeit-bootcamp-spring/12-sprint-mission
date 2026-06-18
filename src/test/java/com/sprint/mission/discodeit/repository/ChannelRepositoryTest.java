package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@EnableJpaAuditing
@DataJpaTest
public class ChannelRepositoryTest {

	@Autowired
	private ChannelRepository channelRepository;


	@Test
	@DisplayName("findAllByTypeOrIdIn - 성공: 지정한 타입(PUBLIC)과 일치하거나 ID 목록에 포함된 채널을 모두 조회한다")
	void findAllByTypeOrIdIn_Success() {
		// given
		Channel publicChannel = new Channel(ChannelType.PUBLIC, "공개 광장", "모두를 위한 채널");
		channelRepository.save(publicChannel);
		Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
		channelRepository.save(privateChannel);
		Channel geometryChannel = new Channel(ChannelType.PRIVATE, null, null);
		channelRepository.save(geometryChannel);
		List<UUID> searchIds = List.of(privateChannel.getId());

		// when
		List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, searchIds);

		// then
		assertThat(publicChannel.getCreatedAt()).isNotNull();
		assertThat(privateChannel.getCreatedAt()).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactlyInAnyOrder(publicChannel, privateChannel);
		assertThat(result).doesNotContain(geometryChannel);
	}

	@Test
	@DisplayName("findAllByTypeOrIdIn - 성공: ID 목록이 비어있어도 지정한 타입(PUBLIC)의 채널은 정상적으로 조회한다")
	void findAllByTypeOrIdIn_Success_WithEmptyIds() {
		// given
		Channel publicChannel1 = new Channel(ChannelType.PUBLIC, "채널A", "설명A");
		Channel publicChannel2 = new Channel(ChannelType.PUBLIC, "채널B", "설명B");
		Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
		channelRepository.saveAll(List.of(publicChannel1, publicChannel2, privateChannel));
		List<UUID> emptyIds = List.of();

		// when
		List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, emptyIds);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).containsExactlyInAnyOrder(publicChannel1, publicChannel2);
		assertThat(result).doesNotContain(privateChannel);
	}

	@Test
	@DisplayName("findAllByTypeOrIdIn - 실패/데이터없음: 타입이 일치하는 채널도 없고 ID 목록에 매칭되는 채널도 없으면 빈 리스트를 반환한다")
	void findAllByTypeOrIdIn_Fail_NoMatch() {
		// given
		Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
		channelRepository.save(privateChannel);
		List<UUID> nonExistentIds = List.of(UUID.randomUUID());

		// when
		List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, nonExistentIds);

		// then
		assertThat(result).isEmpty();
	}

}
