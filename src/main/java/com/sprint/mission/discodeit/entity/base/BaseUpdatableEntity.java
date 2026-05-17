package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
public class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    @Column(name = "updated_at")
    protected Instant updatedAt;

    protected BaseUpdatableEntity() {}

    protected BaseUpdatableEntity(UUID id) {
        super(id);
    }
}
