package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    protected UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;

    protected BaseEntity() {}

    protected BaseEntity(UUID id) {
        this.id = id;
    }
}
