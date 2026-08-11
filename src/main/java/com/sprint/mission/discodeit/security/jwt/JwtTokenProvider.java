package com.sprint.mission.discodeit.security.jwt;

import com.github.f4b6a3.uuid.UuidCreator;
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
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  // 브라우저에 내려줄 리프레시 토큰 쿠키 이름
  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  // ms 단위로 저장
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;

  // 토큰 발급자(issuer) 식별용 문자열
  private final String issuer;

  // Access / Refresh 각각에 사용할 서명/검증 객체
  private final JWSSigner accessTokenSigner;
  private final JWSVerifier accessTokenVerifier;
  private final JWSSigner refreshTokenSigner;
  private final JWSVerifier refreshTokenVerifier;

  public JwtTokenProvider(@Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.access-token-validity-seconds}") long accessTokenValiditySeconds,
      @Value("${security.jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds,
      @Value("${security.jwt.issuer}") String issuer) throws JOSEException {
    this.issuer = issuer;
    this.accessTokenExpirationMs = accessTokenValiditySeconds * 1000L;
    this.refreshTokenExpirationMs = refreshTokenValiditySeconds * 1000L;

    byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

    this.accessTokenSigner = new MACSigner(secretBytes);
    this.accessTokenVerifier = new MACVerifier(secretBytes);
    this.refreshTokenSigner = new MACSigner(secretBytes);
    this.refreshTokenVerifier = new MACVerifier(secretBytes);
  }

  // Access Token 생성 메서드(단기, json으로 응답 예정)
  public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
  }

  // Refresh Token 생성 메서드(장기, 쿠키로 응답 예정)
  public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
  }

  // 공용 토큰 만드는 메서드
  private String generateToken(
      DiscodeitUserDetails userDetails,
      long expirationMs,
      JWSSigner signer,
      String tokenType
  ) throws JOSEException {
    String tokenId = UuidCreator.getTimeOrderedEpoch().toString();
    UserDto user = userDetails.getUserDto();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMs);

    // 클레임 설계
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(user.username())
        .jwtID(tokenId)
        .issuer(issuer)
        .claim("userId", user.id())
        .claim("type", tokenType)
        .claim("username", user.username())
        .claim("email", user.email())
        .claim("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()))
        .issueTime(now)
        .expirationTime(expiryDate)
        .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJWT.sign(signer);
    String token = signedJWT.serialize();
    log.debug("{} 토큰 발급 완료: username={}", tokenType, user.username());
    return token;
  }

  // Access  토큰 검증
  public boolean validateAccessToken(String token) {
    return validateToken(token, accessTokenVerifier, "access");
  }

  // Refresh  토큰 검증
  public boolean validateRefreshToken(String token) {
    return validateToken(token, refreshTokenVerifier, "refresh");
  }

  // 토큰 검증 공용
  private boolean validateToken(String token, JWSVerifier verifier, String expectedType) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      // 1) 서명 검증
      if (!signedJWT.verify(verifier)) {
        log.debug("{} 토큰 서명 검증 실패", expectedType);
        return false;
      }

      // 2) type 클레임 검증
      String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
      if (!expectedType.equals(tokenType)) {
        log.debug("JWT 토큰 타입 불일치: expected={}, actual={}", expectedType, tokenType);
        return false;
      }

      // 3) 만료 시간
      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime == null || expirationTime.before(new Date())) {
        log.debug("{} 토큰 만료됨", expectedType);
        return false;
      }

      return true;
    } catch (Exception e) {
      log.debug("{} 토큰 검증 실패: {}", expectedType, e.getMessage());
      return false;
    }
  }

  // username 파싱
  public String getUsernameFromToken(String token) {
    try {
      SignedJWT signedJWT = parseAndVerify(token, accessTokenVerifier, "access");
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.", e);
    }
  }

  public String getTokenId(String token) {
    try {
      SignedJWT signedJWT = parseAndVerify(token, accessTokenVerifier, "access");
      return signedJWT.getJWTClaimsSet().getJWTID();
    } catch (Exception e) {
      throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.", e);
    }
  }

  public UUID getUserId(String token) {
    try {
      SignedJWT signedJWT = parseAndVerify(token, accessTokenVerifier, "access");
      Object claim = signedJWT.getJWTClaimsSet().getClaim("userId");

      if (claim instanceof UUID uuid) {
        return uuid;
      }
      if (claim instanceof String str) {
        return UUID.fromString(str);
      }

      throw new IllegalArgumentException("JWT 토큰에 userId 클레임이 없습니다.");
    } catch (Exception e) {
      throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.", e);
    }
  }


  public JwtObject parseAccessToken(String token) {
    return parseInternal(token, accessTokenVerifier, "access");
  }

  public JwtObject parseRefreshToken(String token) {
    return parseInternal(token, refreshTokenVerifier, "refresh");
  }

  private JwtObject parseInternal(String token, JWSVerifier verifier, String expectedType) {
    try {
      SignedJWT signedJWT = parseAndVerify(token, verifier, expectedType);
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      // 1) 간단한 Type 검증!
      String actualType = claimsSet.getStringClaim("type");
      if (!expectedType.equals(actualType)) {
        throw new IllegalArgumentException("JWT Type 불일치!");
      }

      // 2) 파싱 과정 (반드시 선언한 클레임을 모두 가져올 것!)
      // -> 미션에서는 DB 조회가 필요할텐데, 강사는 비권장!
      UUID userId = UUID.fromString(claimsSet.getStringClaim("userId"));
      String username = claimsSet.getSubject(); // sub
      String email = claimsSet.getStringClaim("email");
      Date exp = claimsSet.getExpirationTime(); // exp
      Date issueTime = claimsSet.getIssueTime(); // iat
      List<String> roleList = claimsSet.getStringListClaim("roles");

      Role primaryRole = Role.valueOf(roleList.get(0).substring(5));
      UserDto userDto = new UserDto(userId, username, email, null, true, primaryRole);
      return new JwtObject(issueTime.toInstant(), exp.toInstant(), userDto, token);
    } catch (Exception e) {
      throw new IllegalArgumentException("JWT 파싱 실패!", e);
    }

  }

  private SignedJWT parseAndVerify(String token, JWSVerifier verifier, String expectedType) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      if (!signedJWT.verify(verifier)) {
        throw new IllegalArgumentException("JWT 서명 검증 실패");
      }

      String actualType = signedJWT.getJWTClaimsSet().getStringClaim("type");
      if (!expectedType.equals(actualType)) {
        throw new IllegalArgumentException("JWT Type 불일치!");
      }

      Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (exp == null || exp.before(new Date())) {
        throw new IllegalArgumentException("JWT 만료됨");
      }

      return signedJWT;
    } catch (ParseException | JOSEException e) {
      throw new IllegalArgumentException("JWT 파싱/검증 실패", e);
    }
  }

  // 생성된 Refresh 토큰을 쿠키로 변환하는 과정 (보안에서 가장 중요한 코드!! ★★★★★)
  public Cookie genereateRefreshTokenCookie(String refreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken); // 키쌍으로 생성!
    cookie.setHttpOnly(true); // https에서만 보호됨
    cookie.setSecure(true); // 브라우저(=JS)에서 쿠키 읽기 방지, XSS 방지용
    cookie.setPath("/");
    cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000L)); // 쿠키 저장 시간
    return cookie;
  }

  // 생성된 Refresh 무효화 하는 쿠키로 만드는 과정
  public Cookie genereateRefreshTokenExpirationCookie() {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, ""); // 값 지우기!
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(0); // 무효화 시간!
    return cookie;
  }
}
