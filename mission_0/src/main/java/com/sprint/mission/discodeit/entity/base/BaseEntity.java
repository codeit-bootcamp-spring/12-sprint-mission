package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.AuditingBeanDefinitionParser;


@Getter
@MappedSuperclass
@EntityListeners(AuditingBeanDefinitionParser.class)
public abstract class BaseEntity {

  @Id
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false,
      updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant updatedAt;

  //편의를 위한 생성자 추가 (새로운 엔티티를 만들 때 ID를 직접부여하기 위함)
  protected BaseEntity() {
    this.id = UUID.randomUUID();
  }

}
