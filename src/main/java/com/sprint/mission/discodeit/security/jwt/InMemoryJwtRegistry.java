package com.sprint.mission.discodeit.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin =
            new ConcurrentHashMap<>();

    private final Set<String> accessTokenIndexes =
            ConcurrentHashMap.newKeySet();

    private final Set<String> refreshTokenIndexes =
            ConcurrentHashMap.newKeySet();

    private final int maxActiveJwtCount;
    private final JwtTokenProvider jwtTokenProvider;

    public InMemoryJwtRegistry(
            @Value("${discodeit.jwt.max-active-jwt-count:1}")
            int maxActiveJwtCount,
            JwtTokenProvider jwtTokenProvider
    ) {
        if (maxActiveJwtCount < 1) {
            throw new IllegalArgumentException(
                    "최대 활성 JWT 수는 1 이상이어야 합니다."
            );
        }

        this.maxActiveJwtCount = maxActiveJwtCount;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        origin.compute(userId, (key, existingQueue) -> {
            Queue<JwtInformation> queue = existingQueue == null
                    ? new ConcurrentLinkedQueue<>()
                    : existingQueue;

            while (queue.size() >= maxActiveJwtCount) {
                JwtInformation removedJwtInformation = queue.poll();

                if (removedJwtInformation != null) {
                    removeTokenIndexes(removedJwtInformation);
                }
            }

            queue.offer(jwtInformation);
            addTokenIndexes(jwtInformation);

            return queue;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.computeIfPresent(userId, (key, queue) -> {
            queue.forEach(this::removeTokenIndexes);
            queue.clear();

            return null;
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);

        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessTokenIndexes.contains(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshTokenIndexes.contains(refreshToken);
    }

    @Override
    public void rotateJwtInformation(
            String oldRefreshToken,
            JwtInformation newJwtInformation
    ) {
        UUID userId = newJwtInformation.getUserDto().id();
        AtomicBoolean rotated = new AtomicBoolean(false);

        origin.computeIfPresent(userId, (key, queue) -> {
            queue.stream()
                    .filter(jwtInformation ->
                            Objects.equals(
                                    jwtInformation.getRefreshToken(),
                                    oldRefreshToken
                            )
                    )
                    .findFirst()
                    .ifPresent(jwtInformation -> {
                        removeTokenIndexes(jwtInformation);

                        jwtInformation.rotate(
                                newJwtInformation.getAccessToken(),
                                newJwtInformation.getRefreshToken()
                        );

                        addTokenIndexes(jwtInformation);
                        rotated.set(true);
                    });

            return queue;
        });

        if (!rotated.get()) {
            throw new IllegalArgumentException(
                    "활성화된 리프레시 토큰을 찾을 수 없습니다."
            );
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.forEach((userId, ignoredQueue) ->
                origin.computeIfPresent(userId, (key, queue) -> {
                    queue.removeIf(jwtInformation -> {
                        boolean expired = isRefreshTokenExpired(jwtInformation);

                        if (expired) {
                            removeTokenIndexes(jwtInformation);
                        }

                        return expired;
                    });

                    return queue.isEmpty() ? null : queue;
                })
        );
    }

    private boolean isRefreshTokenExpired(JwtInformation jwtInformation) {
        return !jwtTokenProvider.validateRefreshToken(
                jwtInformation.getRefreshToken()
        );
    }

    private void addTokenIndexes(JwtInformation jwtInformation) {
        accessTokenIndexes.add(jwtInformation.getAccessToken());
        refreshTokenIndexes.add(jwtInformation.getRefreshToken());
    }

    private void removeTokenIndexes(JwtInformation jwtInformation) {
        accessTokenIndexes.remove(jwtInformation.getAccessToken());
        refreshTokenIndexes.remove(jwtInformation.getRefreshToken());
    }
}