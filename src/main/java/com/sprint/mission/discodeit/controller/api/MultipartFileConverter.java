package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartFileConverter {

    public static BinaryContentCreateRequest toRequest(MultipartFile file) {
        try {
            return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        }
    }
}