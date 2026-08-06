package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            @Value("${security.jwt.access-token-validity-seconds}") long accessTokenExpirationMs,
            @Value("${security.jwt.refresh-token-validity-seconds}") long refreshTokenExpirationMs,
            @Value("${security.jwt.issuer}") String issuer
    ) throws JOSEException {
        this.issuer = issuer;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        this.accessTokenSigner = new MACSigner(secretBytes);
        this.accessTokenVerifier = new MACVerifier(secretBytes);
        this.refreshTokenSigner = new MACSigner(secretBytes);
        this.refreshTokenVerifier = new MACVerifier(secretBytes);
    }

    // access token 생성
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException{
        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    // refresh token 생성
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException{
        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    // 토큰 생성 공용 메소드
    private String generateToken(
            DiscodeitUserDetails userDetails,
            long expirationMs,
            JWSSigner signer,
            String tokenType
    ) throws JOSEException{
        String tokenId = UUID.randomUUID().toString();
        UserDto user = userDetails.getUserDto();

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expirationMs);

        // 클레임
        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                .subject(user.username())
                .jwtID(tokenId)
                .issuer(issuer)
                .claim("userId", user.id().toString())
                .claim("type", tokenType)
                .claim("email", user.email())
                .claim("profile", user.profile())
                .claim("online", user.online())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issueTime(now)
                .expirationTime(expireDate)
                .build();

        SignedJWT signedJWT = new SignedJWT (new JWSHeader(JWSAlgorithm.HS256), claimSet);
        signedJWT.sign(signer);
        String token = signedJWT.serialize();
        log.debug("Generated {} token for user: {}", tokenType, user.username());
        return token;
    }

    public boolean validateAccessToken(String token ){
        return validateToken(token, accessTokenVerifier, "access");
    }

    public boolean validateRefreshToken(String token){
        return validateToken(token, refreshTokenVerifier,"refresh");
    }

    // 토큰 검증
    private boolean validateToken(
            String token,
            JWSVerifier verifier,
            String expectedType
    ) {
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 서명 검증
            if(!signedJWT.verify(verifier)){
                log.debug("JWT signature verification failed for {} token", expectedType);
                return false;
            }

            // 토큰 유형 검사
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if(!expectedType.equals(tokenType)){
                log.debug("JWT token type mismatch: expected {}, got {}", expectedType, tokenType);
                return false;
            }

            // 토큰 유효기간 검증
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if(expirationTime == null || expirationTime.before(new Date())) {
                log.debug("JWT {} token expired", expectedType);
                return false;
            }

            return true;

        } catch (Exception e) {
            log.debug("JWT {} token validation failed: {}", expectedType, e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public String getTokenId(String token){
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token", e);
        }
    }

    public UUID getUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Object claim = signedJWT.getJWTClaimsSet().getClaim("userId");

            if (claim instanceof String str) {
                return UUID.fromString(str);
            }
            if (claim instanceof UUID userId) {
                return userId;
            }
            throw new IllegalArgumentException("User ID claim not found in JWT token");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    // refresh 토큰을 쿠키로 변환
    public Cookie generateRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true); // XSS 방지용
        cookie.setSecure(true); // https 요청에서만 쿠키를 전송
        cookie.setPath("/"); // 모든 경로의 요청에 쿠키 포함
        cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000L)); // 쿠키 저장시간
        return cookie;
    }

    // 만료된 refresh 토큰 생성 ( refresh 토큰 무효화)
    public Cookie generateRefreshTokenExpirationCookie() {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public JwtObject parseAccessToken(String token) {
        return parseInternal(token, "access");
    }

    public JwtObject parseRefreshToken(String token) {
        return parseInternal(token, "refresh");
    }

    private JwtObject parseInternal(String token, String expectedType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // 토큰 유형 검증
            String actualType = claimsSet.getStringClaim("type");
            if (!expectedType.equals(actualType)) {
                throw new IllegalArgumentException("JWT Type 불일치");
            }

            // 클레임 내용 파싱
            UUID userId = UUID.fromString(claimsSet.getClaim("userId").toString());
            String username = claimsSet.getSubject(); // sub
            String email = claimsSet.getStringClaim("email");
            BinaryContentDto profile = (BinaryContentDto) claimsSet.getClaim("profile");
            Boolean online = (Boolean) claimsSet.getClaim("online");
            Date exp = claimsSet.getExpirationTime(); // exp
            Date issueTime = claimsSet.getIssueTime(); // iat
            List<String> roleList = claimsSet.getStringListClaim("roles"); // role

            Role primaryRole = Role.valueOf(roleList.get(0).substring(5));
            UserDto userDto = new UserDto( userId, username, email, profile,online, primaryRole);
            return new JwtObject(issueTime.toInstant(), exp.toInstant(), userDto, token);

        } catch (Exception e) {
            throw new IllegalArgumentException("JWT 파싱 실패!", e);
        }
    }
}
