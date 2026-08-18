package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

// bytes 제거 - DB에는 메타 정보만 저장, 실제 파일은 BinaryContentStorage에서 관리
// 업로드 결과를 status에 기록하므로 수정 시점이 필요하다 → BaseUpdatableEntity
@Getter
@NoArgsConstructor
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false)
  private String contentType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BinaryContentStatus status;

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    // 메타 데이터만 저장된 시점이므로 업로드는 아직 진행 중이다
    this.status = BinaryContentStatus.PROCESSING;
  }

  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
  }
}
