package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends UpdatableBaseEntity {

    private static final long ONLINE_THRESHOLD_MINUTES = 5;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user) {
        super();
        this.user = user;
        this.lastActiveAt = Instant.now();
    }

    public boolean isOnline() {
        return Duration.between(this.lastActiveAt, Instant.now()).toMinutes()
            < ONLINE_THRESHOLD_MINUTES;
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
    }
}
