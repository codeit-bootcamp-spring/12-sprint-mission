package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

  private String fileName;
  private Long size;
  private String contentType;
  private byte[] bytes;

  protected BinaryContent() {
  }

  public BinaryContent(String fileName,
                       Long size,
                       String contentType,
                       byte[] bytes) {

    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }
}