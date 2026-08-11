package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("PUBLIC 채널과 지정한 PRIVATE 채널 목록 조회 성공")
    void findAllByTypeOrIdIn_success() {
        // given
        Channel publicChannel1 = Channel.createPublic("public-1", "description-1");
        Channel publicChannel2 = Channel.createPublic("public-2", "description-2");
        Channel privateChannel1 = Channel.createPrivate();
        Channel privateChannel2 = Channel.createPrivate();


        // when
        Channel savedPublicChannel1 = channelRepository.save(publicChannel1);
        Channel savedPublicChannel2 = channelRepository.save(publicChannel2);
        Channel savedPrivateChannel1 = channelRepository.save(privateChannel1);
        Channel savedPrivateChannel2 = channelRepository.save(privateChannel2);

        channelRepository.flush();
        entityManager.clear();

        List<Channel> result = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                List.of(savedPrivateChannel1.getId())
        );


        // then
        assertThat(result)
                .extracting(Channel::getId)
                .containsExactlyInAnyOrder(
                        savedPublicChannel1.getId(),
                        savedPublicChannel2.getId(),
                        savedPrivateChannel1.getId()
                );

        assertThat(result)
                .extracting(Channel::getId)
                .doesNotContain(savedPrivateChannel2.getId());
    }

    @Test
    @DisplayName("PUBLIC 채널만 조회 성공 - PRIVATE 채널 ID 목록이 비어 있음")
    void findAllByTypeOrIdIn_success_publicOnlyWhenIdsEmpty() {
        // given
        Channel publicChannel = Channel.createPublic("public", "description");
        Channel privateChannel = Channel.createPrivate();


        // when
        Channel savedPublicChannel = channelRepository.save(publicChannel);
        Channel savedPrivateChannel = channelRepository.save(privateChannel);

        channelRepository.flush();
        entityManager.clear();

        List<Channel> result = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                List.of()
        );


        // then
        assertThat(result)
                .extracting(Channel::getId)
                .containsExactly(savedPublicChannel.getId());

        assertThat(result)
                .extracting(Channel::getId)
                .doesNotContain(savedPrivateChannel.getId());
    }

    @Test
    @DisplayName("채널 단건 조회 성공")
    void findById_success() {
        // given
        Channel channel = Channel.createPublic("public", "description");


        // when
        Channel savedChannel = channelRepository.saveAndFlush(channel);
        entityManager.clear();

        Optional<Channel> result = channelRepository.findById(savedChannel.getId());


        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedChannel.getId());
        assertThat(result.get().getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.get().getName()).isEqualTo("public");
        assertThat(result.get().getDescription()).isEqualTo("description");
    }

    @Test
    @DisplayName("채널 단건 조회 실패 - 존재하지 않음")
    void findById_fail_notFound() {
        // given

        // when
        Optional<Channel> result = channelRepository.findById(UUID.randomUUID());


        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 저장 성공 - PUBLIC")
    void save_success_publicChannel() {
        // given
        Channel channel = Channel.createPublic("public", "description");


        // when
        Channel savedChannel = channelRepository.saveAndFlush(channel);
        entityManager.clear();

        Optional<Channel> result = channelRepository.findById(savedChannel.getId());


        // then
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.get().getName()).isEqualTo("public");
        assertThat(result.get().getDescription()).isEqualTo("description");
        assertThat(result.get().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("채널 저장 성공 - PRIVATE")
    void save_success_privateChannel() {
        // given
        Channel channel = Channel.createPrivate();


        // when
        Channel savedChannel = channelRepository.saveAndFlush(channel);
        entityManager.clear();

        Optional<Channel> result = channelRepository.findById(savedChannel.getId());


        // then
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(ChannelType.PRIVATE);
        assertThat(result.get().getName()).isNull();
        assertThat(result.get().getDescription()).isNull();
        assertThat(result.get().getCreatedAt()).isNotNull();
    }
}
