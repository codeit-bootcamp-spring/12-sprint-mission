package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// @MappedSuperclass: 별도 테이블 없이 하위 엔티티 테이블에 컬럼 포함
// @EntityListeners: @CreatedDate/@LastModifiedDate 자동 처리
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  protected UUID id;

  // @CreatedDate: 최초 저장 시점을 JPA Auditing이 자동으로 기록
  @CreatedDate
  @Column(nullable = false, updatable = false)
  protected Instant createdAt;
}
