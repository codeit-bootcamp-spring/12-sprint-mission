package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;

public interface JwtRegistry<T> {

    void registerJwtInformation(JwtInformation jwtInformation);

    void invalidateJwtInformationByUserId(T userId); // 무효화 메소드

    boolean hasActiveJwtInformationByUserId(T userId);

    boolean hasActiveJwtInformationAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void clearExpiredJwtInformation();
}

