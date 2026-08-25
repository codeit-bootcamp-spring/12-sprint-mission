package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry<UUID>{

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

    private final int maxActiveJwtCount;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    // 새 JWT 정보를 registry에 등록. maxActiveJwtCount 초과시 오래된 토큰 제거. (queue는 FIFO이기 때문에 poll로 제거 가능)
    public void registerJwtInformation(JwtInformation jwtInformation) {
        origin.compute(jwtInformation.getUserDto().id(), (key,queue) ->{
            if(queue==null){
                queue = new ConcurrentLinkedQueue<>();
            }

            if(queue.size() >= maxActiveJwtCount) {
                JwtInformation deprecatedJwtInformation = queue.poll();
                if(deprecatedJwtInformation!=null){
                    removeTokenIndex(
                            deprecatedJwtInformation.getAccessToken(),
                            deprecatedJwtInformation.getRefreshToken()
                    );
                }
            }

            queue.add(jwtInformation);
            addTokenIndex(
                    jwtInformation.getAccessToken(),
                    jwtInformation.getRefreshToken()
            );
            return queue;
        });
    }

    @Override
    // JWT 무효화 및 유효 토큰 목록에서 제거
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.computeIfPresent(userId, (key,queue)->{
            queue.forEach(jwtInformation ->
                    removeTokenIndex(
                    jwtInformation.getAccessToken(),
                    jwtInformation.getRefreshToken()
            ));
            queue.clear();
            return null;
        });
    }

    @Override
    // refresh 토큰을 기준으로 jwt 정보를 새 jwt로 교체
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.computeIfPresent(newJwtInformation.getUserDto().id() , (key,queue)->{
            queue.stream().filter(jwtInformation
                    -> jwtInformation.getRefreshToken().equals(refreshToken))
                    .findFirst()
                    .ifPresent(jwtInformation -> {
                        removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
                        jwtInformation.rotate(
                                newJwtInformation.getAccessToken(),
                                newJwtInformation.getRefreshToken()
                        );
                        addTokenIndex(
                                newJwtInformation.getAccessToken(),
                                newJwtInformation.getRefreshToken()
                        );
                    });
            return queue;
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.entrySet().removeIf(entry ->{
            Queue<JwtInformation> queue = entry.getValue();
            queue.removeIf(jwtInformation -> {
                boolean isExpired =
                !jwtTokenProvider.validateAccessToken(jwtInformation.getAccessToken()) ||
                        !jwtTokenProvider.validateRefreshToken(jwtInformation.getRefreshToken());
                if(isExpired){
                    removeTokenIndex(
                            jwtInformation.getAccessToken(),
                            jwtInformation.getRefreshToken()
                    );
                }
                return isExpired;
            });
            return queue.isEmpty();
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return origin.containsKey(userId);
    }

    @Override
    public boolean hasActiveJwtInformationAccessToken(String accessToken) {
        return accessTokenIndexes.contains(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshTokenIndexes.contains(refreshToken);
    }


    private void addTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.add(accessToken);
        refreshTokenIndexes.add(refreshToken);
    }

    private void removeTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.remove(accessToken);
        refreshTokenIndexes.remove(refreshToken);
    }
}
