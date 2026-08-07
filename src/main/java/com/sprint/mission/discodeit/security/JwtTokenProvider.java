package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final String TOKEN_TYPE = "type";
  private static final String ROLES = "roles";
  private static final String ACCESS = "access";
  private static final String REFRESH = "refresh";

  private final byte[] secret;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;
  // 단일 인스턴스용 재사용 방지 목록. 다중 서버 운영 시 Redis로 교체
  private final Set<String> usedRefreshTokenIds = ConcurrentHashMap.newKeySet();

  public JwtTokenProvider(
      @Value("${discodeit.jwt.secret}") String secret,
      @Value("${discodeit.jwt.access-token-expiration}") long accessTokenExpiration,
      @Value("${discodeit.jwt.refresh-token-expiration}") long refreshTokenExpiration
  ) {
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
    if (this.secret.length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
    }
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(userDetails, ACCESS, accessTokenExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(userDetails, REFRESH, refreshTokenExpiration);
  }

  public String refreshAccessToken(String refreshToken) {
    String subject = consumeRefreshToken(refreshToken);
    JWTClaimsSet claims = parseAndValidate(refreshToken);
    return generateToken(
        subject,
        ((List<?>) claims.getClaim(ROLES)).stream().map(String::valueOf).toList(),
        ACCESS,
        accessTokenExpiration
    );
  }

  public String consumeRefreshToken(String refreshToken) {
    JWTClaimsSet claims = parseAndValidate(refreshToken);
    if (!REFRESH.equals(claims.getClaim(TOKEN_TYPE))
        || claims.getJWTID() == null
        || !usedRefreshTokenIds.add(claims.getJWTID())) {
      throw new IllegalArgumentException("Invalid refresh token");
    }
    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      parseAndValidate(token);
      return true;
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }

  public boolean validateAccessToken(String token) {
    try {
      return ACCESS.equals(parseAndValidate(token).getClaim(TOKEN_TYPE));
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }

  public String getSubject(String token) {
    return parseAndValidate(token).getSubject();
  }

  private String generateToken(UserDetails userDetails, String type, long expiration) {
    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
    return generateToken(userDetails.getUsername(), roles, type, expiration);
  }

  private String generateToken(
      String subject,
      List<String> roles,
      String type,
      long expiration
  ) {
    Instant now = Instant.now();
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(subject)
        .claim(ROLES, roles)
        .claim(TOKEN_TYPE, type)
        .jwtID(UUID.randomUUID().toString())
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plusSeconds(expiration)))
        .build();
    SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
    try {
      jwt.sign(new MACSigner(secret));
      return jwt.serialize();
    } catch (JOSEException exception) {
      throw new IllegalStateException("Failed to sign JWT", exception);
    }
  }

  private JWTClaimsSet parseAndValidate(String token) {
    try {
      SignedJWT jwt = SignedJWT.parse(token);
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())
          || !jwt.verify(new MACVerifier(secret))
          || claims.getExpirationTime() == null
          || !claims.getExpirationTime().after(new Date())) {
        throw new IllegalArgumentException("Invalid JWT");
      }
      return claims;
    } catch (ParseException | JOSEException | RuntimeException exception) {
      if (exception instanceof IllegalArgumentException illegalArgumentException) {
        throw illegalArgumentException;
      }
      throw new IllegalArgumentException("Invalid JWT", exception);
    }
  }
}
