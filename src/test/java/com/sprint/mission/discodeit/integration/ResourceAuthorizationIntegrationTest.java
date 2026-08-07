package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ResourceAuthorizationIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private MessageRepository messageRepository;

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void userCanOnlyModifyOwnInformation() {
    User owner = saveUser("owner", "owner@example.com");
    User other = saveUser("other", "other@example.com");
    authenticate(owner);

    UserDto updated = userService.update(
        owner.getId(),
        new UserUpdateRequest("updated-owner", "updated@example.com", null),
        Optional.empty()
    );

    assertThat(updated.username()).isEqualTo("updated-owner");
    assertThatThrownBy(() -> userService.delete(other.getId()))
        .isInstanceOf(AccessDeniedException.class);
  }

  @Test
  void userCanOnlyModifyOwnMessages() {
    User author = saveUser("author", "author@example.com");
    User other = saveUser("reader", "reader@example.com");
    Channel channel = channelRepository.save(
        new Channel(ChannelType.PUBLIC, "channel", "description")
    );
    Message message = messageRepository.save(new Message("before", channel, author, List.of()));

    authenticate(author);
    assertThat(messageService.update(message.getId(), new MessageUpdateRequest("after")).content())
        .isEqualTo("after");

    authenticate(other);
    assertThatThrownBy(() -> messageService.delete(message.getId()))
        .isInstanceOf(AccessDeniedException.class);
  }

  private User saveUser(String username, String email) {
    return userRepository.save(new User(username, email, "password", null));
  }

  private void authenticate(User user) {
    UserDto userDto = new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        null,
        false,
        user.getRole()
    );
    DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, user.getPassword());
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
    );
  }
}
