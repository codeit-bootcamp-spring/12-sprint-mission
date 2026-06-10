package com.sprint.mission.discodeit.entity.channel;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "channels")
@Getter @ToString(callSuper = true) @SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChannelType type;

    private Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String name, String description) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
    }

    public static Channel createPublic(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description);
    }

    public static Channel createPrivate() {
        return new Channel(ChannelType.PRIVATE, null, null);
    }

}
