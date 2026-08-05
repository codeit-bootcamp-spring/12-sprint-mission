package com.sprint.mission.discodeit.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.TestSecuritySupport;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceMockTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private ChannelMapper channelMapper;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private RoleHierarchy roleHierarchy;

  @InjectMocks
  private BasicChannelService channelService;

  private PublicChannelCreateRequest publicChannelCreateRequest;
  private PrivateChannelCreateRequest privateChannelCreateRequest;

  private List<UserDto> participants;
  private User user1;
  private User user2;
  private Channel publicChannel;
  private Channel privateChannel;
  private ChannelDto publicChannelDto;
  private ChannelDto privateChannelDto;

  @BeforeEach
  void setUp() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    user1 = User.builder()
        .id(userId1)
        .username("user1")
        .email("user1@test.com")
        .password("password")
        .build();

    user2 = User.builder()
        .id(userId2)
        .username("user2")
        .email("user2@test.com")
        .password("password")
        .build();

    UserDto userDto1 = new UserDto(userId1, "user1", "user1@test.com", null, false);
    UserDto userDto2 = new UserDto(userId2, "user2", "user2@test.com", null, false);

    participants = List.of(userDto1, userDto2);

    publicChannelCreateRequest = new PublicChannelCreateRequest(
        "publicChannel",
        "description"
    );

    privateChannelCreateRequest = new PrivateChannelCreateRequest(
        List.of(
            userId1,
            userId2
        )
    );

    publicChannel = Channel.builder()
        .id(UUID.randomUUID())
        .name("publicChannel")
        .description("description")
        .type(ChannelType.PUBLIC)
        .build();

    privateChannel = Channel.builder()
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .type(ChannelType.PRIVATE)
        .build();

    publicChannelDto = new ChannelDto(
        UUID.randomUUID(),
        publicChannel.getType(),
        publicChannel.getName(),
        publicChannel.getDescription(),
        null,
        null
    );

    privateChannelDto = new ChannelDto(
        UUID.randomUUID(),
        privateChannel.getType(),
        null,
        null,
        null,
        participants
    );

    TestSecuritySupport.authenticate(userId1, Role.CHANNEL_MANAGER);
    lenient().when(roleHierarchy.getReachableGrantedAuthorities(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @AfterEach
  void tearDown() {
    TestSecuritySupport.clear();
  }

  @Test
  @DisplayName("publicChannel_create_success")
  void publicChannel_create_success() {
    given(channelMapper.toEntity(any())).willReturn(publicChannel);
    given(channelRepository.save(any())).willReturn(publicChannel);
    given(messageRepository.findTopByChannelIdOrderByCreatedAtDesc(publicChannel.getId()))
        .willReturn(Optional.empty());
    given(readStatusRepository.findAllByChannelId(publicChannel.getId())).willReturn(List.of());
    given(channelMapper.toDto(eq(publicChannel), isNull(), eq(List.<UserDto>of())))
        .willReturn(publicChannelDto);

    ChannelDto result = channelService.create(publicChannelCreateRequest);

    assertNotNull(result);
    assertEquals(publicChannelDto, result);
  }

  @Test
  @DisplayName("publicChannel_create_failed")
  void publicChannel_create_failed() {
    given(channelRepository.existsByName(any())).willReturn(true);

    assertThatThrownBy(() -> channelService.create(publicChannelCreateRequest))
        .isInstanceOf(ChannelNameAlreadyExistsException.class);
  }

  @Test
  @DisplayName("privateChannel_create_success")
  void privateChannel_create_success() {
    List<ReadStatus> readStatuses = List.of(
        new ReadStatus(user1, privateChannel, privateChannel.getCreatedAt()),
        new ReadStatus(user2, privateChannel, privateChannel.getCreatedAt())
    );

    given(channelRepository.save(any())).willReturn(privateChannel);
    given(userRepository.findAllById(any())).willReturn(List.of(user1, user2));
    given(messageRepository.findTopByChannelIdOrderByCreatedAtDesc(any()))
        .willReturn(Optional.empty());
    given(readStatusRepository.findAllByChannelId(any())).willReturn(
        readStatuses);
    given(userMapper.toDto(user1)).willReturn(participants.get(0));
    given(userMapper.toDto(user2)).willReturn(participants.get(1));
    given(channelMapper.toDto(eq(privateChannel), isNull(), eq(participants)))
        .willReturn(privateChannelDto);

    ChannelDto result = channelService.create(privateChannelCreateRequest);

    assertNotNull(result);
    assertEquals(privateChannelDto, result);
  }

  @Test
  @DisplayName("privateChannel_create_failed")
  void privateChannel_create_failed() {
    UUID missingUserId = UUID.randomUUID();
    privateChannelCreateRequest = new PrivateChannelCreateRequest(
        List.of(user1.getId(), user2.getId(), missingUserId)
    );

    given(channelRepository.save(any())).willReturn(privateChannel);
    given(userRepository.findAllById(any())).willReturn(List.of(user1, user2));

    assertThatThrownBy(() -> channelService.create(privateChannelCreateRequest))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("channel_findByUserId_success")
  void channel_findByUserId_success() {
    UUID userId = user1.getId();

    Instant publicLastMessageAt = Instant.now();
    Instant privateLastMessageAt = Instant.now().minusSeconds(60);

    ReadStatus readStatus1 = new ReadStatus(user1, privateChannel, Instant.now());
    ReadStatus readStatus2 = new ReadStatus(user2, privateChannel, Instant.now());

    publicChannel.setReadStatuses(List.of());
    privateChannel.setReadStatuses(List.of(readStatus1, readStatus2));

    given(channelRepository.findAllByUserIdAndPublicChannelsWithParticipants(userId))
        .willReturn(List.of(publicChannel, privateChannel));

    given(messageRepository.findLastMessageAtByChannelIds(
        List.of(publicChannel.getId(), privateChannel.getId())
    )).willReturn(List.of(
        new Object[]{publicChannel.getId(), publicLastMessageAt},
        new Object[]{privateChannel.getId(), privateLastMessageAt}
    ));

//    privateChannel
    given(userMapper.toDto(user1)).willReturn(participants.get(0));
    given(userMapper.toDto(user2)).willReturn(participants.get(1));

    given(channelMapper.toDto(eq(publicChannel), eq(publicLastMessageAt), eq(List.<UserDto>of())))
        .willReturn(publicChannelDto);

    given(channelMapper.toDto(eq(privateChannel), eq(privateLastMessageAt), eq(participants)))
        .willReturn(privateChannelDto);

    List<ChannelDto> result = channelService.findAllByUserId(userId);

    assertEquals(List.of(publicChannelDto, privateChannelDto), result);
  }

  @Test
  @DisplayName("channel_findByUserId_failed")
  void channel_findByUserId_failed() {
    assertThatThrownBy(() -> channelService.findAllByUserId(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("channel_update_success")
  void channel_update_success() {
    UUID publicChannelId = publicChannel.getId();
    ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(
        "update_channel_name",
        "update_channel_description"
    );

    given(channelRepository.findById(publicChannelId)).willReturn(Optional.of(publicChannel));
    given(channelRepository.existsByName(channelUpdateRequest.newName()))
        .willReturn(false);
    given(channelRepository.save(any(Channel.class))).willAnswer(inv -> inv.getArgument(0));
    given(messageRepository.findTopByChannelIdOrderByCreatedAtDesc(publicChannel.getId()))
        .willReturn(Optional.empty());
    given(readStatusRepository.findAllByChannelId(publicChannel.getId())).willReturn(List.of());
    given(channelMapper.toDto(eq(publicChannel), isNull(), eq(List.<UserDto>of())))
        .willReturn(publicChannelDto);

    ChannelDto result = channelService.update(publicChannelId, channelUpdateRequest);

    assertEquals(publicChannelDto, result);
    assertEquals("update_channel_name", publicChannel.getName());
    assertEquals("update_channel_description", publicChannel.getDescription());
  }

  @Test
  @DisplayName("channel_update_failed")
  void channel_update_failed() {
    UUID privateChannelId = privateChannel.getId();
    ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(
        "update_channel_name",
        "update_channel_description"
    );

    given(channelRepository.findById(privateChannelId)).willReturn(Optional.of(privateChannel));

    assertThatThrownBy(() -> channelService.update(privateChannelId, channelUpdateRequest))
        .isInstanceOf(PrivateChannelUpdateNotAllowedException.class);
  }

  @Test
  @DisplayName("channel_delete_success")
  void channel_delete_success() {
    given(channelRepository.findById(any())).willReturn(Optional.of(publicChannel));

    channelService.delete(publicChannel.getId());

    verify(channelRepository).delete(publicChannel);
  }

  @Test
  @DisplayName("channel_delete_failed")
  void channel_delete_failed() {
    given(channelRepository.findById(any())).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.delete(publicChannel.getId()))
        .isInstanceOf(ChannelNotFoundException.class);
  }
}
