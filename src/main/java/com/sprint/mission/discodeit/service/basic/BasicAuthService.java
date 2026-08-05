package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  //  세션 무효를 구현하다 Session_Manager로 책임 이동.
//  일단 서비스는 살려둠.
  private final SessionRegistry sessionRegistry;

}
