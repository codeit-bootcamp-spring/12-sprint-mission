package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "read_statuses", uniqueConstraints = {
            @UniqueConstraint(name = "uk_read_statuses_user_channel", columnNames = {"user_id", "channel_id"})
        })
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    protected ReadStatus() {}

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        super(UUID.randomUUID());
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt == null ? Instant.now() : lastReadAt;
    }

    public void update(Instant newLastReadAt) {
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
        }
    }
}
