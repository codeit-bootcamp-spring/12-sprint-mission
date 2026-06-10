package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String fileName;
    @Column(nullable = false, length = 100)
    private String contentType;
    @Column(nullable = false)
    private Long size;

    public BinaryContent(String fileName, String contentType, Long size) {
        super();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }

}
