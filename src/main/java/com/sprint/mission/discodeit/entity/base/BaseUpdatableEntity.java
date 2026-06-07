package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

// @LastModifiedDate: save() 호출마다 JPA Auditing이 자동으로 시점 기록
@Getter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  @Column
  protected Instant updatedAt;
}
