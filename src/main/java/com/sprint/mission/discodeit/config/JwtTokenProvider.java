package com.sprint.mission.discodeit.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.jwt.JwtException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * jwt token 발급, 갱신, 유효성 검사 담당 컴포넌트
 */
@Component
public class JwtTokenProvider {

  private static final String TOKEN_TYPE_CLAIM = "tokenType";
  private static final String ACCESS_TOKEN = "access";
  private static final String REFRESH_TOKEN = "refresh";
  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private final SecretKey signingKey;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
      @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
  ) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length < 32) {
      throw new JwtException(ErrorCode.JWT_INVALID_CONFIGURATION);
    }

    if (accessTokenExpiration <= 0 || refreshTokenExpiration <= 0) {
      throw new JwtException(ErrorCode.JWT_INVALID_CONFIGURATION);
    }

    this.signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  public String createAccessToken(UserResponse user) {
    return createToken(user.id(), user.username(), user.role().name(), ACCESS_TOKEN,
        accessTokenExpiration);
  }

  public String createRefreshToken(UserResponse user) {
    return createToken(user.id(), user.username(), user.role().name(), REFRESH_TOKEN,
        refreshTokenExpiration);

  }

  public String refreshAccessToken(String refreshToken) {
    JWTClaimsSet claims = getValidatedClaims(refreshToken, REFRESH_TOKEN);
    try {
      return createToken(
          UUID.fromString(claims.getSubject()),
          claims.getStringClaim("username"),
          claims.getStringClaim("role"),
          ACCESS_TOKEN,
          accessTokenExpiration
      );
    } catch (ParseException | IllegalArgumentException e) {
      throw new JwtException(ErrorCode.JWT_INVALID_TOKEN, e);
    }
  }

  // 토큰 재발급
  public TokenPair refreshTokens(String refreshToken) {
    JWTClaimsSet claims = getValidatedClaims(refreshToken, REFRESH_TOKEN);

    try {
      UUID userId = UUID.fromString(claims.getSubject());
      String username = claims.getStringClaim("username");
      String role = claims.getStringClaim("role");

      return new TokenPair(
          createToken(userId, username, role, ACCESS_TOKEN, accessTokenExpiration),
          createToken(userId, username, role, REFRESH_TOKEN, refreshTokenExpiration),
          username
      );

    } catch (ParseException | IllegalArgumentException e) {
      throw new JwtException(ErrorCode.JWT_INVALID_TOKEN, e);
    }
  }

  public boolean validateToken(String token) {
    try {
      getValidatedClaims(token, null);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  public JWTClaimsSet getAccessTokenClaims(String token) {
    return getValidatedClaims(token, ACCESS_TOKEN);
  }

  public JWTClaimsSet getRefreshTokenClaims(String token) {
    return getValidatedClaims(token, REFRESH_TOKEN);
  }

  public long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }

  public record TokenPair(String accessToken, String refreshToken, String username) {

  }

  private String createToken(
      UUID userId, String username, String role, String tokenType, long expiration
  ) {
    Instant now = Instant.now();
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(userId.toString())
        .claim("username", username)
        .claim("role", role)
        .claim(TOKEN_TYPE_CLAIM, tokenType)
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plusMillis(expiration)))
        .build();

    try {
      SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      signedJwt.sign(new MACSigner(signingKey));
      return signedJwt.serialize();
    } catch (JOSEException e) {
      throw new JwtException(ErrorCode.JWT_TOKEN_GENERATION_FAILED, e);
    }
  }

  private JWTClaimsSet getValidatedClaims(String token, String requiredTokenType) {
    try {
      SignedJWT signedJwt = SignedJWT.parse(token);
      if (!JWSAlgorithm.HS256.equals(signedJwt.getHeader().getAlgorithm())
          || !signedJwt.verify(new MACVerifier(signingKey))) {
        throw new JwtException(ErrorCode.JWT_INVALID_TOKEN);
      }

      JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
      Date expirationTime = claims.getExpirationTime();
      if (expirationTime == null || !expirationTime.toInstant().isAfter(Instant.now())) {
        throw new JwtException(ErrorCode.JWT_EXPIRED_TOKEN);
      }

      if (requiredTokenType != null
          && !requiredTokenType.equals(claims.getStringClaim(TOKEN_TYPE_CLAIM))) {
        throw new JwtException(ErrorCode.JWT_INVALID_TOKEN_TYPE);
      }

      return claims;

    } catch (ParseException | JOSEException e) {
      throw new JwtException(ErrorCode.JWT_INVALID_TOKEN, e);
    }
  }
}
