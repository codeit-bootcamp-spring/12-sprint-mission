package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "read_statuses",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_read_statuses_user_channel",
                    columnNames = {"user_id", "channel_id"}
            )
        })
@Getter @ToString(callSuper = true) @SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    public void updateLastReadAt(Instant lastReadAt) {
        if (lastReadAt != null) {
            this.lastReadAt = lastReadAt;
        }
    }

}
