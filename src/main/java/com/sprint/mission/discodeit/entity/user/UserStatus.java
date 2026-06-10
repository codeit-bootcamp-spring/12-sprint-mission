package com.sprint.mission.discodeit.entity.user;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "user_statuses")
@Getter @ToString(callSuper = true) @SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false , unique = true)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    public void updateLastActiveAt(Instant lastActiveAt) {
        if (lastActiveAt != null) {
            this.lastActiveAt = lastActiveAt;
        }
    }

    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minus(ONLINE_THRESHOLD));
    }


}