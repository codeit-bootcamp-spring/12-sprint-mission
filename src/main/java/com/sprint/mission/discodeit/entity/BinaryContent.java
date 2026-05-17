package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    protected BinaryContent() {}

    public BinaryContent(String fileName, Long size, String contentType) {
        super(UUID.randomUUID());
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }
}
