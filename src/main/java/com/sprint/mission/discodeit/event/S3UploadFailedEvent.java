package com.sprint.mission.discodeit.event;

import java.util.UUID;

/**
 * 재시도까지 모두 실패한 바이너리 업로드를 관리자에게 알리기 위한 이벤트.
 *
 * <p>비동기 작업의 실패는 사용자 요청과 다른 스레드/다른 시점에 일어나 응답에 실릴 수 없다.
 * 사후 디버깅에 필요한 정보를 이벤트에 모두 담아 둔다.
 */
public record S3UploadFailedEvent(
    // 실패한 작업 이름
    String taskName,
    // MDC의 Request ID - 실패한 업로드를 원래 요청 로그와 이어붙이는 열쇠
    String requestId,
    UUID binaryContentId,
    // 실패 이유 (예외 메시지)
    String errorMessage
) {

}
