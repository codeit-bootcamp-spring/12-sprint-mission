package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    private final JWSSigner signer;
    private final JWSVerifier verifier;

    private final String issuer;

    private final boolean refreshTokenCookieSecure;

    public JwtTokenProvider(
            @Value("${discodeit.jwt.secret}")
            String encodedSecret,

            @Value("${discodeit.jwt.issuer}")
            String issuer,

            @Value("${discodeit.jwt.access-token-validity-ms}")
            long accessTokenValidityMs,

            @Value("${discodeit.jwt.refresh-token-validity-ms}")
            long refreshTokenValidityMs,

            @Value("${discodeit.jwt.refresh-token-cookie-secure:false}")
            boolean refreshTokenCookieSecure
    ) {
        byte[] secret = Base64.getDecoder().decode(encodedSecret);

        try {
            this.signer = new MACSigner(secret);
            this.verifier = new MACVerifier(secret);
        } catch (JOSEException e) {
            throw new IllegalArgumentException("JWT 비밀키가 올바르지 않습니다.", e);
        }

        this.issuer = issuer;
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
        this.refreshTokenCookieSecure = refreshTokenCookieSecure;
    }

    public String generateAccessToken(UUID userId) {
        return generateToken(userId, ACCESS_TOKEN_TYPE, accessTokenValidityMs);
    }

    public String generateRefreshToken(UUID userId) {
        return generateToken(userId, REFRESH_TOKEN_TYPE, refreshTokenValidityMs);
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, ACCESS_TOKEN_TYPE);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, REFRESH_TOKEN_TYPE);
    }

    public UUID getUserIdFromAccessToken(String token) {
        return getUserId(token, ACCESS_TOKEN_TYPE);
    }

    public UUID getUserIdFromRefreshToken(String token) {
        return getUserId(token, REFRESH_TOKEN_TYPE);
    }

    public Cookie generateRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(refreshTokenCookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(Math.toIntExact(refreshTokenValidityMs / 1000));

        return cookie;
    }

    public Cookie generateRefreshTokenExpirationCookie() {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(refreshTokenCookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);

        return cookie;
    }



    private String generateToken(UUID userId, String tokenType, long validityMs) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(validityMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(userId.toString())
                .jwtID(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("JWT 발급에 실패했습니다.", e);
        }
    }

    private boolean validateToken(String token, String expectedTokenType) {
        try {
            parseAndValidate(token, expectedTokenType);
            return true;
        } catch (ParseException | JOSEException | IllegalArgumentException e) {
            return false;
        }
    }

    private UUID getUserId(String token, String expectedTokenType) {
        try {
            JWTClaimsSet claimsSet = parseAndValidate(token, expectedTokenType);

            return UUID.fromString(claimsSet.getSubject());
        } catch (ParseException | JOSEException | IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT에서 사용자 ID를 가져올 수 없습니다.", e);
        }
    }

    private JWTClaimsSet parseAndValidate(String token, String expectedTokenType) throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = parseAndVerify(token);

        Date expirationTime = claimsSet.getExpirationTime();

        if (expirationTime == null || !expirationTime.after(new Date())) {
            throw new IllegalArgumentException("JWT가 만료되었습니다.");
        }

        String actualTokenType = claimsSet.getStringClaim(TOKEN_TYPE_CLAIM);

        if (!expectedTokenType.equals(actualTokenType)) {
            throw new IllegalArgumentException("JWT 종류가 올바르지 않습니다.");
        }

        if (claimsSet.getSubject() == null) {
            throw new IllegalArgumentException("JWT에 사용자 ID가 없습니다.");
        }

        return claimsSet;
    }

    private JWTClaimsSet parseAndVerify(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (!signedJWT.verify(verifier)) {
            throw new IllegalArgumentException("JWT 서명이 올바르지 않습니다.");
        }

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        if (!issuer.equals(claimsSet.getIssuer())) {
            throw new IllegalArgumentException("JWT 발급자가 올바르지 않습니다.");
        }

        return claimsSet;
    }



}
