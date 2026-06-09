package com.sprint.mission.discodeit.dto.response;

import java.util.List;

// 커서 기반 페이지네이션 응답
// nextCursor: 다음 페이지 요청 시 cursor 파라미터로 사용 (마지막 항목의 createdAt 등)
// number(page offset) 제거 → 커서 방식에서는 페이지 번호 개념 없음
public record PageResponse<T>(
    List<T> content,
    Object nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
) {

}
