package com.sprint.mission.discodeit.common.util;

import java.util.Objects;
import java.util.Optional;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import org.springframework.web.multipart.MultipartFile;

public class BinaryContentRequestConverter {
    public static Optional<BinaryContentCreateRequest> convert(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }

        String contentType = Objects.requireNonNull(file).getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType) || "image/webp".equals(contentType))) {
            return Optional.empty();
        }

        try {
            BinaryContentCreateRequest profileCreateRequest = new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    contentType,
                    file.getBytes()
            );
            return Optional.of(profileCreateRequest);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
