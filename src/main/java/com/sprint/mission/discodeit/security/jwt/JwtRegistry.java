package com.sprint.mission.discodeit.security.jwt;

import java.util.Optional;
import java.util.UUID;

/**
 * 발급된 토큰의 상태를 관리한다.
 *
 * <p>JWT는 무상태라 서버가 발급 이후를 통제할 수 없다. 로그아웃, 강제 만료, 동시 로그인 제한을 하려면
 * 세션 기반에서 SessionRegistry가 하던 역할을 대신할 저장소가 필요하다.
 *
 * <p>"활성"의 기준은 메소드마다 다르다. 로그인 여부(ByUserId)는 엑세스 토큰이 살아있는지로 판단하고,
 * 정리(clearExpired)는 리프레시 토큰까지 만료되어 재발급조차 불가능해진 시점을 기준으로 한다.
 */
public interface JwtRegistry {

  /** 로그인 성공 시 등록한다. 최대 동시 로그인 수를 초과하면 가장 오래된 로그인을 무효화한다. */
  void registerJwtInformation(JwtInformation jwtInformation);

  /** 해당 사용자의 모든 토큰을 무효화한다. 권한 변경, 리프레시 토큰 재사용 감지 시 사용한다. */
  void invalidateJwtInformationByUserId(UUID userId);

  /** 리프레시 토큰이 가리키는 로그인 하나만 무효화한다. 로그아웃에서 사용한다. */
  void invalidateJwtInformationByRefreshToken(String refreshToken);

  /** 엑세스 토큰이 살아있는 로그인이 있는지. 사용자의 온라인 여부 판단에 사용한다. */
  boolean hasActiveJwtInformationByUserId(UUID userId);

  /**
   * 엑세스 토큰으로 등록 정보를 찾는다. 필터가 DB 조회 없이 Principal을 복원하기 위해 사용한다.
   *
   * <p>"활성인지 확인" 후 "찾기"로 나누면 그 사이에 로테이션·로그아웃이 끼어들 수 있고 서명 검증도
   * 중복되므로, 조회 한 번으로 두 목적을 모두 해결한다.
   */
  Optional<JwtInformation> findJwtInformationByAccessToken(String accessToken);

  /**
   * 리프레시 토큰을 새 토큰 쌍으로 교체한다.
   *
   * <p>교체 가능 여부 확인과 교체가 하나의 원자적 연산으로 수행되며, 결과로 정상 로테이션인지
   * 유예 창 안의 중복 요청인지 재사용인지를 알려준다.
   */
  RotationResult rotateJwtInformation(String refreshToken, String newAccessToken,
      String newRefreshToken);

  /** 리프레시 토큰까지 만료되어 되살릴 수 없는 등록 정보를 삭제한다. */
  void clearExpiredJwtInformation();
}
