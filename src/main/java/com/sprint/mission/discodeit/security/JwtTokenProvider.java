package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;

@Slf4j
@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;
  private final String issuer;

  private final JWSSigner accessTokenSigner;
  private final JWSVerifier accessTokenVerifier;
  private final JWSSigner refreshTokenSigner;
  private final JWSVerifier refreshTokenVerifier;

  public JwtTokenProvider(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.access-token-validity-seconds}") long accessTokenValiditySeconds,
      @Value("${security.jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds,
      @Value("${security.jwt.issuer}") String issuer
  ) throws JOSEException {
    this.issuer = issuer;
    this.accessTokenExpirationMs = accessTokenValiditySeconds * 1000L;
    this.refreshTokenExpirationMs = refreshTokenValiditySeconds * 1000L;

    byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

    this.accessTokenSigner = new MACSigner(secretBytes);
    this.accessTokenVerifier = new MACVerifier(secretBytes);
    this.refreshTokenSigner = new MACSigner(secretBytes);
    this.refreshTokenVerifier = new MACVerifier(secretBytes);
  }

  public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
  }

  public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
  }

  private String generateToken(
      DiscodeitUserDetails userDetails,
      long expirationMs,
      JWSSigner signer,
      String tokenType
  ) throws JOSEException {

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMs);

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(userDetails.getUsername())
        .jwtID(UUID.randomUUID().toString())
        .issuer(issuer)
        .claim("userId", userDetails.getUserDto().id().toString())
        .claim("type", tokenType)
        .claim("email", userDetails.getUserDto().email())
        .claim(
            "roles",
            userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        )
        .issueTime(now)
        .expirationTime(expiryDate)
        .build();

    SignedJWT signedJWT =
        new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

    signedJWT.sign(signer);

    return signedJWT.serialize();
  }

  public boolean validateAccessToken(String token) {
    return validateToken(token, accessTokenVerifier, "access");
  }

  public boolean validateRefreshToken(String token) {
    return validateToken(token, refreshTokenVerifier, "refresh");
  }

  private boolean validateToken(
      String token,
      JWSVerifier verifier,
      String expectedType
  ) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      if (!signedJWT.verify(verifier)) {
        return false;
      }

      String tokenType =
          signedJWT.getJWTClaimsSet().getStringClaim("type");

      if (!expectedType.equals(tokenType)) {
        return false;
      }

      Date expirationTime =
          signedJWT.getJWTClaimsSet().getExpirationTime();

      return expirationTime != null
          && expirationTime.after(new Date());

    } catch (Exception e) {
      log.debug("JWT validation failed: {}", e.getMessage());
      return false;
    }
  }

  public String getUsernameFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid token");
    }
  }

  public UUID getUserId(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      String userId =
          signedJWT.getJWTClaimsSet().getStringClaim("userId");

      return UUID.fromString(userId);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid token", e);
    }
  }

  public Cookie generateRefreshTokenCookie(String refreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000L));

    return cookie;
  }

  public Cookie generateRefreshTokenExpirationCookie() {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);

    return cookie;
  }
}