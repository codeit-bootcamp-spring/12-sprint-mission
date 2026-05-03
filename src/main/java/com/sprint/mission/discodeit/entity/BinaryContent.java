package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class BinaryContent implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;
  private Instant createdAt;
  private byte[] bytes;

  private String fileName;
  private String contentType;
  private Long size;

  public BinaryContent(String filename, String contentType, Long size, byte[] bytes) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();

    this.fileName = filename;
    this.contentType = contentType;
    this.size = size;
    this.bytes = bytes;

  }
}
