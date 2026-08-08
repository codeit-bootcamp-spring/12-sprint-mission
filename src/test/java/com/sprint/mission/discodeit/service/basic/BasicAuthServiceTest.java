package com.sprint.mission.discodeit.service.basic;


import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService authService;

  private UUID userId;
  private String username;
  private String password;
  private String email;
  private User user;
  private UserDto userDto;

  @BeforeEach
  public void setUp() {
    userId = UuidCreator.getTimeOrderedEpoch();
    username = "testUser";
    password = "password1234!";
    email = "test@test.com";
    user = new User(username, email, password, null, Role.USER);
    ReflectionTestUtils.setField(user, "id", userId);
    userDto = new UserDto(userId, username, email, null, true, Role.USER);
  }

}
