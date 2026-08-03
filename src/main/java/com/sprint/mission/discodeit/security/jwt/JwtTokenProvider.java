package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * 토큰 발급, 갱신, 유효성 검사를 담당한다.
 *
 * <p>HS256(대칭키) 서명을 사용한다. 발급자와 검증자가 같은 애플리케이션이므로 비대칭키가 필요 없고,
 * 키 하나만 관리하면 된다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private static final JWSAlgorithm ALGORITHM = JWSAlgorithm.HS256;
  private static final String CLAIM_USERNAME = "username";
  private static final String CLAIM_EMAIL = "email";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TYPE = "type";
  private static final String TYPE_ACCESS = "ACCESS";
  private static final String TYPE_REFRESH = "REFRESH";

  private final JWSSigner signer;
  private final JWSVerifier verifier;
  private final long accessTokenValiditySeconds;
  @Getter
  private final long refreshTokenValiditySeconds;
  private final boolean cookieSecure;

  public JwtTokenProvider(
      @Value("${discodeit.security.jwt.secret}") String secret,
      @Value("${discodeit.security.jwt.access-token-validity-seconds}")
      long accessTokenValiditySeconds,
      @Value("${discodeit.security.jwt.refresh-token-validity-seconds}")
      long refreshTokenValiditySeconds,
      @Value("${discodeit.security.jwt.cookie-secure}") boolean cookieSecure
  ) {
    byte[] key = secret.getBytes(StandardCharsets.UTF_8);
    // HS256은 최소 256비트(32바이트) 키를 요구한다. 짧은 키는 기동 시점에 바로 잡는다.
    if (key.length < 32) {
      throw new IllegalArgumentException(
          "discodeit.security.jwt.secret은 32바이트 이상이어야 합니다. 현재: " + key.length);
    }
    try {
      this.signer = new MACSigner(key);
      this.verifier = new MACVerifier(key);
    } catch (JOSEException e) {
      throw new IllegalStateException("JWT 서명 키 초기화에 실패했습니다.", e);
    }
    this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    this.cookieSecure = cookieSecure;
  }

  /** 사용자 정보로 엑세스/리프레시 토큰 한 쌍을 발급한다. */
  public JwtInformation generate(UserDto userDto) {
    return new JwtInformation(
        userDto,
        generateToken(userDto, TYPE_ACCESS, accessTokenValiditySeconds),
        generateToken(userDto, TYPE_REFRESH, refreshTokenValiditySeconds)
    );
  }

  public boolean isValidAccessToken(String token) {
    return isValid(token, TYPE_ACCESS);
  }

  public boolean isValidRefreshToken(String token) {
    return isValid(token, TYPE_REFRESH);
  }

  /** 서명이 위조되지 않았고, 만료되지 않았으며, 용도가 일치하는지 검사한다. */
  private boolean isValid(String token, String expectedType) {
    if (token == null || token.isBlank()) {
      return false;
    }
    try {
      SignedJWT jwt = SignedJWT.parse(token);
      if (!jwt.verify(verifier)) {
        log.debug("JWT 서명 검증 실패");
        return false;
      }
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      // 엑세스 토큰을 리프레시 토큰처럼 쓰는 것을 막는다
      if (!expectedType.equals(claims.getStringClaim(CLAIM_TYPE))) {
        log.debug("JWT 용도 불일치: expected={}", expectedType);
        return false;
      }
      Date expiresAt = claims.getExpirationTime();
      return expiresAt != null && expiresAt.toInstant().isAfter(Instant.now());
    } catch (ParseException | JOSEException e) {
      log.debug("JWT 파싱/검증 실패: {}", e.getMessage());
      return false;
    }
  }

  /** 만료 여부만 검사한다. 레지스트리 정리에서 사용한다. */
  public boolean isExpired(String token) {
    Instant expiresAt = getExpiresAt(token);
    return expiresAt == null || !expiresAt.isAfter(Instant.now());
  }

  public Instant getExpiresAt(String token) {
    return parseClaims(token)
        .map(claims -> claims.getExpirationTime() == null ? null
            : claims.getExpirationTime().toInstant())
        .orElse(null);
  }

  /** 서명이 유효한 토큰에서 사용자 ID를 꺼낸다. 유효하지 않으면 null. */
  public UUID getUserId(String token) {
    return parseClaims(token)
        .map(claims -> UUID.fromString(claims.getSubject()))
        .orElse(null);
  }

  public ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return refreshTokenCookieBuilder(refreshToken, refreshTokenValiditySeconds).build();
  }

  /** maxAge=0으로 브라우저가 즉시 쿠키를 폐기하도록 한다. */
  public ResponseCookie createExpiredRefreshTokenCookie() {
    return refreshTokenCookieBuilder("", 0).build();
  }

  private ResponseCookie.ResponseCookieBuilder refreshTokenCookieBuilder(String value,
      long maxAgeSeconds) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, value)
        // JS에서 읽을 수 없어야 XSS로 리프레시 토큰이 새어나가지 않는다
        .httpOnly(true)
        // 운영(HTTPS)에서는 true로 켜서 평문 전송을 막는다
        .secure(cookieSecure)
        .path("/")
        .sameSite("Lax")
        .maxAge(maxAgeSeconds);
  }

  private String generateToken(UserDto userDto, String type, long validitySeconds) {
    Instant now = Instant.now();
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(userDto.id().toString())
        .claim(CLAIM_USERNAME, userDto.username())
        .claim(CLAIM_EMAIL, userDto.email())
        .claim(CLAIM_ROLE, userDto.role().name())
        .claim(CLAIM_TYPE, type)
        // 같은 사용자가 같은 초에 재발급받아도 토큰 문자열이 겹치지 않도록 고유 ID를 넣는다
        // (레지스트리가 토큰 문자열을 식별자로 쓰기 때문에 중복되면 상태가 꼬인다)
        .jwtID(UUID.randomUUID().toString())
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plusSeconds(validitySeconds)))
        .build();

    SignedJWT jwt = new SignedJWT(new JWSHeader(ALGORITHM), claims);
    try {
      jwt.sign(signer);
    } catch (JOSEException e) {
      throw new IllegalStateException("JWT 서명에 실패했습니다.", e);
    }
    return jwt.serialize();
  }

  private Optional<JWTClaimsSet> parseClaims(String token) {
    if (token == null || token.isBlank()) {
      return Optional.empty();
    }
    try {
      SignedJWT jwt = SignedJWT.parse(token);
      if (!jwt.verify(verifier)) {
        return Optional.empty();
      }
      return Optional.of(jwt.getJWTClaimsSet());
    } catch (ParseException | JOSEException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }

}
