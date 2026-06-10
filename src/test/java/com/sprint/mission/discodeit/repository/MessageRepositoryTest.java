package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("채널 ID로 메시지 목록 조회 성공")
    void findAllByChannel_Id_success() {
        Channel channel = channelRepository.saveAndFlush(
                Channel.createPublic("channel", "description")
        );

        User author = userRepository.saveAndFlush(
                User.builder()
                        .username("user1")
                        .email("user1@test.com")
                        .password("password")
                        .build()
        );

        Message message1 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message1")
                .build();

        Message message2 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message2")
                .build();

        messageRepository.saveAllAndFlush(List.of(message1, message2));
        entityManager.clear();

        List<Message> result = messageRepository.findAllByChannel_Id(channel.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Message::getContent)
                .containsExactlyInAnyOrder("message1", "message2");
    }

    @Test
    @DisplayName("채널 ID로 메시지 목록 조회 실패 - 존재하지 않는 채널")
    void findAllByChannel_Id_empty() {
        List<Message> result = messageRepository.findAllByChannel_Id(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널의 최신 메시지 조회 성공")
    void findFirstByChannel_IdOrderByCreatedAtDesc_success() {
        Channel channel = channelRepository.saveAndFlush(
                Channel.createPublic("channel", "description")
        );

        User author = userRepository.saveAndFlush(
                User.builder()
                        .username("user1")
                        .email("user1@test.com")
                        .password("password")
                        .build()
        );

        Message oldMessage = Message.builder()
                .channel(channel)
                .author(author)
                .content("old-message")
                .build();

        messageRepository.saveAndFlush(oldMessage);

        Message latestMessage = Message.builder()
                .channel(channel)
                .author(author)
                .content("latest-message")
                .build();

        messageRepository.saveAndFlush(latestMessage);
        entityManager.clear();

        Optional<Message> result = messageRepository.findFirstByChannel_IdOrderByCreatedAtDesc(channel.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo("latest-message");
    }

    @Test
    @DisplayName("채널의 최신 메시지 조회 실패 - 메시지 없음")
    void findFirstByChannel_IdOrderByCreatedAtDesc_empty() {
        Channel channel = channelRepository.saveAndFlush(
                Channel.createPublic("channel", "description")
        );

        entityManager.clear();

        Optional<Message> result = messageRepository.findFirstByChannel_IdOrderByCreatedAtDesc(channel.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 ID로 메시지 최신순 페이징 조회 성공")
    void findAllByChannel_IdOrderByCreatedAtDesc_success() {
        Channel channel = channelRepository.saveAndFlush(
                Channel.createPublic("channel", "description")
        );

        User author = userRepository.saveAndFlush(
                User.builder()
                        .username("user1")
                        .email("user1@test.com")
                        .password("password")
                        .build()
        );

        Message message1 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message1")
                .build();

        messageRepository.saveAndFlush(message1);

        Message message2 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message2")
                .build();

        messageRepository.saveAndFlush(message2);

        Message message3 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message3")
                .build();

        messageRepository.saveAndFlush(message3);
        entityManager.clear();

        List<Message> result = messageRepository.findAllByChannel_IdOrderByCreatedAtDesc(
                channel.getId(),
                PageRequest.of(0, 2)
        );

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Message::getContent)
                .containsExactly("message3", "message2");
    }

    @Test
    @DisplayName("커서 기준으로 채널 메시지 최신순 조회 성공")
    void findAllByChannel_IdAndCreatedAtLessThanOrderByCreatedAtDesc_success() {
        Channel channel = channelRepository.saveAndFlush(
                Channel.createPublic("channel", "description")
        );

        User author = userRepository.saveAndFlush(
                User.builder()
                        .username("user1")
                        .email("user1@test.com")
                        .password("password")
                        .build()
        );

        Message message1 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message1")
                .build();

        messageRepository.saveAndFlush(message1);

        Message message2 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message2")
                .build();

        messageRepository.saveAndFlush(message2);

        Message message3 = Message.builder()
                .channel(channel)
                .author(author)
                .content("message3")
                .build();

        messageRepository.saveAndFlush(message3);
        entityManager.clear();

        Instant cursor = message3.getCreatedAt();

        List<Message> result = messageRepository.findAllByChannel_IdAndCreatedAtLessThanOrderByCreatedAtDesc(
                channel.getId(),
                cursor,
                PageRequest.of(0, 2)
        );

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Message::getContent)
                .containsExactly("message2", "message1");
    }
}