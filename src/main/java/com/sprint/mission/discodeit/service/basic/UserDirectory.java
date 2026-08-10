package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 목록에서 DB에 저장된 부분만 캐싱한다.
 *
 * <p>{@code UserDto.online}은 엔티티 컬럼이 아니라 조회 시점에 JwtRegistry에 물어 계산하는 값이다.
 * 이걸 그대로 캐시에 넣으면 엑세스 토큰이 만료돼 오프라인이 된 사용자가 TTL이 끝날 때까지
 * 온라인으로 표시된다(로그아웃과 달리 만료에는 무효화를 걸 이벤트가 없다).
 * 그래서 캐시에는 online을 비운 채 담고, 실제 값은 {@link BasicUserService#findAll()}에서 채운다.
 *
 * <p>자기 호출은 프록시를 타지 않아 {@code @Cacheable}이 동작하지 않으므로, 캐시 대상 메소드는
 * 서비스와 별도의 빈으로 분리한다.
 */
@RequiredArgsConstructor
@Component
public class UserDirectory {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Cacheable(cacheNames = CacheConfig.USERS)
  @Transactional(readOnly = true)
  public List<UserDto> findAllWithoutOnline() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .map(dto -> new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(),
            null, dto.role()))
        .toList();
  }
}
