package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    String contentType,
    Long size
) {

  public static BinaryContentResponse from(BinaryContent file) {
    return new BinaryContentResponse(
        file.getId(),
        file.getFileName(),
        file.getContentType(),
        file.getSize()
    );
  }

}
