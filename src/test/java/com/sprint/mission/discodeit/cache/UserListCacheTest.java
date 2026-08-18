package com.sprint.mission.discodeit.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserOnlineChecker;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserListCacheTest {

  @Autowired UserService userService;
  @Autowired CacheManager cacheManager;
  @MockitoSpyBean UserRepository userRepository;
  // online 판단은 JwtRegistry를 타므로 테스트에서 직접 제어한다
  @MockitoBean UserOnlineChecker userOnlineChecker;

  @BeforeEach
  void clearCache() {
    cacheManager.getCacheNames()
        .forEach(name -> cacheManager.getCache(name).clear());
  }

  @Test
  @DisplayName("두 번째 조회는 DB를 다시 읽지 않는다")
  void findAll_secondCall_hitsCache() {
    userService.findAll();
    userService.findAll();

    then(userRepository).should(times(1)).findAll();
  }

  @Test
  @DisplayName("캐시가 살아 있어도 online은 매번 다시 계산된다")
  void findAll_onlineIsNotCached() {
    // 관리자 계정이 부트스트랩되어 최소 1명은 존재한다
    List<UserDto> first = userService.findAll();
    assertThat(first).isNotEmpty();
    UUID someUserId = first.get(0).id();

    given(userOnlineChecker.isOnline(someUserId)).willReturn(true);
    assertThat(findById(userService.findAll(), someUserId).online()).isTrue();

    // 로그아웃이나 토큰 만료로 상태가 바뀌어도 캐시를 비우지 않는다
    given(userOnlineChecker.isOnline(someUserId)).willReturn(false);
    assertThat(findById(userService.findAll(), someUserId).online()).isFalse();

    // 그 사이 DB는 한 번만 읽었다
    then(userRepository).should(times(1)).findAll();
  }

  @Test
  @DisplayName("사용자가 추가되면 목록 캐시가 무효화된다")
  void create_evictsCache() {
    int before = userService.findAll().size();

    userService.create(
        new UserCreateRequest("cacheuser", "cacheuser@test.com", "password123!"),
        Optional.empty());

    assertThat(userService.findAll()).hasSize(before + 1);
    assertThat(cacheManager.getCache(CacheConfig.USERS)).isNotNull();
  }

  private UserDto findById(List<UserDto> users, UUID id) {
    return users.stream().filter(user -> user.id().equals(id)).findFirst().orElseThrow();
  }
}
