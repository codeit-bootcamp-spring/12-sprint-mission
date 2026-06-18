package com.sprint.mission.discodeit.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

@ActiveProfiles("test")
@EnableJpaAuditing
@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;


	@Test
	@DisplayName("findByUsername - 성공: 존재하는 username으로 조회 시 해당하는 유저 엔티티를 반환한다")
	void findByUsername_Success() {
		// given
		User user = new User("discode_king", "king@example.com", "password123", null);
		userRepository.save(user);

		// when
		Optional<User> result = userRepository.findByUsername("discode_king");

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getUsername()).isEqualTo("discode_king");
		assertThat(result.get().getEmail()).isEqualTo("king@example.com");
	}

	@Test
	@DisplayName("findByUsername - 실패/데이터없음: 존재하지 않는 username으로 조회 시 Optional.empty()를 반환한다")
	void findByUsername_ReturnsEmpty_WhenUserDoesNotExist() {
		// given

		// when
		Optional<User> result = userRepository.findByUsername("ghost_user");

		// then
		assertThat(result).isEmpty();
	}


	@Test
	@DisplayName("existsByEmail - 성공: 존재하는 이메일로 검사 시 true를 반환한다")
	void existsByEmail_ReturnsTrue_WhenEmailExists() {
		// given
		User user = new User("user1", "exist@example.com", "password123", null);
		userRepository.save(user);

		// when
		boolean result = userRepository.existsByEmail("exist@example.com");

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("existsByEmail - 실패/데이터없음: 존재하지 않는 이메일로 검사 시 false를 반환한다")
	void existsByEmail_ReturnsFalse_WhenEmailDoesNotExist() {
		// given

		// when
		boolean result = userRepository.existsByEmail("notfound@example.com");

		// then
		assertThat(result).isFalse();
	}


	@Test
	@DisplayName("existsByUsername - 성공: 존재하는 username으로 검사 시 true를 반환한다")
	void existsByUsername_ReturnsTrue_WhenUsernameExists() {
		// given
		User user = new User("tester", "tester@example.com", "password123", null);
		userRepository.save(user);

		// when
		boolean result = userRepository.existsByUsername("tester");

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("existsByUsername - 실패/데이터없음: 존재하지 않는 username으로 검사 시 false를 반환한다")
	void existsByUsername_ReturnsFalse_WhenUsernameDoesNotExist() {
		// given

		// when
		boolean result = userRepository.existsByUsername("unknown_tester");

		// then
		assertThat(result).isFalse();
	}


	@Test
	@DisplayName("findAllWithProfileAndStatus - 성공: 연관된 프로필(상태)이 있는 유저 목록을 패치 조인으로 일괄 조회한다")
	void findAllWithProfileAndStatus_Success() {
		// given
		BinaryContent profile = new BinaryContent("profile.png", 1024L, "image/png");
		User user = new User("join_user", "join@example.com", "password123", profile);
		UserStatus status = new UserStatus(user, Instant.now());
		userRepository.save(user);

		// when
		List<User> result = userRepository.findAllWithProfileAndStatus();

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getUsername()).isEqualTo("join_user");
		assertThat(result.get(0).getStatus()).isNotNull();
	}

	@Test
	@DisplayName("findAllWithProfileAndStatus - 실패/데이터없음: 테이블에 유저가 한 명도 없다면 빈 리스트를 반환한다")
	void findAllWithProfileAndStatus_ReturnsEmptyList_WhenNoUsersExist() {
		// given

		// when
		List<User> result = userRepository.findAllWithProfileAndStatus();

		// then
		assertThat(result).isEmpty();
		assertThat(result).isEmpty();
	}

}
